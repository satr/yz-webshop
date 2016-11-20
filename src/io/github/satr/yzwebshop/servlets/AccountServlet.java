package io.github.satr.yzwebshop.servlets;

import io.github.satr.yzwebshop.entities.Account;
import io.github.satr.yzwebshop.helpers.DispatchHelper;
import io.github.satr.yzwebshop.helpers.Env;
import io.github.satr.yzwebshop.helpers.ParameterHelper;
import io.github.satr.yzwebshop.repositories.AccountRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static io.github.satr.yzwebshop.helpers.Env.*;

@WebServlet(value = {"/account/login/*","/account/logout/*","/account/signup/*","/account/detail/*","/account/edit/*",})
public class AccountServlet extends HttpServlet {

    private final AccountRepository accountRepository;
    private final Random random;

    public AccountServlet() {
        accountRepository = new AccountRepository();
        random = new Random(System.currentTimeMillis());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        switch(servletPath) {
            case ActionPath.SIGNUP:
                processSignUp(request, response);
                break;
            case ActionPath.EDIT:
                processEdit(request, response);
                break;
            case ActionPath.LOGIN:
                processLogin(request, response);
                break;
            case ActionPath.LOGOUT:
                DispatchHelper.dispatchHome(request, response);
                break;
            default:
                DispatchHelper.dispatchError(request, response, "Undefined request \"%s\"", servletPath);
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        removeRequestAttr(request, RequestAttr.ACTION);
        switch(request.getServletPath()) {
            case ActionPath.DETAIL:
                showDetail(request, response);
                break;
            case ActionPath.LOGIN:
                dispatchLogin(request, response);
                break;
            case ActionPath.LOGOUT:
                removeSessionAttr(request, Env.SessionAttr.ACCOUNT);
                DispatchHelper.dispatchHome(request, response);
                break;
            case ActionPath.SIGNUP:
                showSignUp(request, response);
                break;
            case ActionPath.EDIT:
                showEdit(request, response);
                break;
            default:
                DispatchHelper.dispatchHome(request, response);
                break;
        }
    }


    private void processLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        String email = ParameterHelper.getString(request, RequestParam.EMAIL, errorMessages);
        String password = ParameterHelper.getString(request, RequestParam.PASSWORD, errorMessages);

        if(!validateCredentialParams(request, response, email, password, errorMessages)
           ||  !authenticate(request, response, email, password, errorMessages)) {
            setRequestAttr(request, RequestAttr.ERRORS, errorMessages);
            dispatchLogin(request, response);
            return;
        }

        DispatchHelper.dispatchHome(request, response);
    }

    private void processSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        EditableAccount editableAccount = new EditableAccount();
        populateFromRequest(request, editableAccount, errorMessages);

        if (!validateSignUpParams(editableAccount, errorMessages)) {
            setRequestAttr(request, RequestAttr.ERRORS, errorMessages);
            setRequestAttr(request, RequestAttr.EDITABLE_ACCOUNT, editableAccount);
            setRequestAttr(request, RequestAttr.ACTION, Action.SIGNUP);
            DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
            return;
        }

        Account account = new Account();
        try {
            account.setCreatedOn(new Timestamp(new Date().getTime()));
            account.setPasswordSalt(createRandomString());
            setAccountPassword(account, editableAccount.getNewPassword());
            updateAccountFromEditable(account, editableAccount);
            accountRepository.save(account);
            account = accountRepository.getByEmail(account.getEmail());
        } catch (Exception e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return;
        }
        setSessionAttr(request, Env.SessionAttr.ACCOUNT, account);
        DispatchHelper.dispatchWebInf(request, response, AccountPage.WELCOME_REGISTERED);
    }

    private void processEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = getSessionAttr(request, Env.SessionAttr.ACCOUNT);
        if(account == null) {
            showEdit(request, response);
            return;
        }

        ArrayList<String> errorMessages = new ArrayList<>();
        EditableAccount editableAccount = new EditableAccount().copyFrom(account);
        populateFromRequest(request, editableAccount, errorMessages);
        editableAccount.setCurrentPassword(ParameterHelper.getString(request, RequestParam.CURRENT_PASSWORD, errorMessages));

        if (!validateEditParams(account, editableAccount, errorMessages)) {
            setRequestAttr(request, RequestAttr.ERRORS, errorMessages);
            setRequestAttr(request, RequestAttr.EDITABLE_ACCOUNT, editableAccount);
            setRequestAttr(request, RequestAttr.ACTION, Action.EDIT);
            DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
            return;
        }

        try {
            if(shouldChangePassword(account, editableAccount))
                setAccountPassword(account, editableAccount.getNewPassword());
            updateAccountFromEditable(account, editableAccount);
            accountRepository.save(account);
        } catch (Exception e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return;
        }

        DispatchHelper.dispatchWebInf(request, response, AccountPage.DETAIL);
    }

    private String createRandomString() {
        return "" + random.nextLong();
    }

    private void updateAccountFromEditable(Account account, EditableAccount editableAccount) throws NoSuchAlgorithmException {
        account.setFirstName(editableAccount.getFirstName());
        account.setMiddleName(editableAccount.getMiddleName());
        account.setLastName(editableAccount.getLastName());
        account.setEmail(editableAccount.getEmail());
        account.setUpdatedOn(new Timestamp(new Date().getTime()));
    }

    private void populateFromRequest(HttpServletRequest request, EditableAccount editableAccount, ArrayList<String> errorMessages) {
        editableAccount.setEmail(ParameterHelper.getString(request, RequestParam.EMAIL, errorMessages));
        editableAccount.setRepeatedEmail(ParameterHelper.getString(request, RequestParam.REPEATED_EMAIL, errorMessages));
        editableAccount.setNewPassword(ParameterHelper.getString(request, RequestParam.PASSWORD, errorMessages));
        editableAccount.setRepeatedPassword(ParameterHelper.getString(request, RequestParam.REPEATED_PASSWORD, errorMessages));
        editableAccount.setFirstName(ParameterHelper.getString(request, RequestParam.FIRST_NAME, errorMessages));
        editableAccount.setMiddleName(ParameterHelper.getString(request, RequestParam.MIDDLE_NAME, errorMessages));
        editableAccount.setLastName(ParameterHelper.getString(request, RequestParam.LAST_NAME, errorMessages));
    }

    private void setAccountPassword(Account account, String password) throws NoSuchAlgorithmException {
        account.setPasswordHash(getHashBy(password, account.getPasswordSalt()));
    }

    private boolean validateSignUpParams(EditableAccount editableAccount, ArrayList<String> errorMessages) throws ServletException, IOException {
        validateNewNames(editableAccount, errorMessages);
        validateNewEmail(editableAccount, errorMessages);
        validateNewPassword(editableAccount, errorMessages);
        return errorMessages.size() == 0;
    }

    private boolean validateEditParams(Account account, EditableAccount editableAccount, ArrayList<String> errorMessages) throws ServletException, IOException {
        validateNewNames(editableAccount, errorMessages);

        if(account.getEmail().compareTo(editableAccount.getEmail()) != 0)
            validateNewEmail(editableAccount, errorMessages);

        if(!isEmptyOrWhitespace(editableAccount.getNewPassword()) || !isEmptyOrWhitespace(editableAccount.getRepeatedPassword())) {
            if(isEmptyOrWhitespace(editableAccount.getCurrentPassword()))
                errorMessages.add("Missed Current Password.");
            else if(!validatePassword(account, editableAccount.getCurrentPassword()))
                errorMessages.add("Invalid Current Password.");
            validateNewPassword(editableAccount, errorMessages);
        }
        return errorMessages.size() == 0;
    }

    private boolean isEmptyOrWhitespace(String value) {
        return value == null || value.trim().length() == 0;
    }

    private void validateNewNames(EditableAccount editableAccount, ArrayList<String> errorMessages) {
        if(isEmptyOrWhitespace(editableAccount.getFirstName()))
            errorMessages.add("Missed First Name.");
        if(isEmptyOrWhitespace(editableAccount.getLastName()))
            errorMessages.add("Missed Last Name.");
    }

    private void validateNewPassword(EditableAccount editableAccount, ArrayList<String> errorMessages) {
        boolean missedNewPassword = isEmptyOrWhitespace(editableAccount.getNewPassword());
        if(missedNewPassword)
            errorMessages.add("Missed Password.");
        boolean missedRepeatedPassword = isEmptyOrWhitespace(editableAccount.getRepeatedPassword());
        if(missedRepeatedPassword)
            errorMessages.add("Missed Repeated Password.");
        if(missedNewPassword || missedRepeatedPassword)
            return;
        if(editableAccount.getNewPassword().compareTo(editableAccount.getRepeatedPassword()) != 0)
            errorMessages.add("Password and Repeated Password do not match.");
    }

    private void validateNewEmail(EditableAccount editableAccount, ArrayList<String> errorMessages) {
        boolean missedEmail = isEmptyOrWhitespace(editableAccount.getEmail());
        if(missedEmail)
            errorMessages.add("Missed Email.");
        boolean missedRepeatedEmail = isEmptyOrWhitespace(editableAccount.getRepeatedEmail());
        if(missedRepeatedEmail)
            errorMessages.add("Missed Repeated Email.");
        if (missedEmail || missedRepeatedEmail)
            return;
        if(editableAccount.getEmail().compareTo(editableAccount.getRepeatedEmail()) != 0)
            errorMessages.add("Email and Repeated Email do not match.");
        else if(checkEmailAlreadyRegistered(editableAccount.getEmail(), errorMessages))
            errorMessages.add("User with this Email already registered.");
    }

    private boolean checkEmailAlreadyRegistered(String email, ArrayList<String> errorMessages) {
        try {
            return accountRepository.getByEmail(email) != null;
        } catch (SQLException e) {
            errorMessages.add(e.getMessage());//TODO -  wrap with user-friendly message and log
        }
        return true;
    }

    private boolean validateCredentialParams(HttpServletRequest request, HttpServletResponse response, String email, String password, List<String> errorMessages) throws ServletException, IOException {
        if(isEmptyOrWhitespace(email))
            errorMessages.add("Missed Email.");

        if(isEmptyOrWhitespace(password))
            errorMessages.add("Missed Password.");

        return errorMessages.size() == 0;
    }

    private boolean authenticate(HttpServletRequest request, HttpServletResponse response, String email, String password, ArrayList<String> errorMessages) throws ServletException, IOException {
        Account account = null;
        try {
            account = accountRepository.getByEmail(email);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return false;
        }

        if(account == null || !validatePassword(account, password)) {
            errorMessages.add("Invalid Email or Password.");
            return false;
        }

        setSessionAttr(request, Env.SessionAttr.ACCOUNT, account);
        return true;
    }

    private boolean validatePassword(Account account, String password) {
        String passwordHash = account.getPasswordHash();
        String passwordSalt = account.getPasswordSalt();
        try {
            String generatedHash = getHashBy(password, passwordSalt);
            return passwordHash != null && passwordHash.compareTo(generatedHash) == 0;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private String getHashBy(String password, String passwordSalt) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(password));
        md5.update(StandardCharsets.UTF_8.encode(passwordSalt));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

    private boolean shouldChangePassword(Account account, EditableAccount editableAccount) {
        return !isEmptyOrWhitespace(editableAccount.getNewPassword()) && !isEmptyOrWhitespace(editableAccount.getRepeatedPassword())
                && editableAccount.getNewPassword().compareTo(editableAccount.getRepeatedPassword()) == 0
                && editableAccount.getCurrentPassword().compareTo(editableAccount.getNewPassword()) != 0
                && validatePassword(account, editableAccount.getCurrentPassword());
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (getSessionAttr(request, Env.SessionAttr.ACCOUNT) == null) {
            dispatchLogin(request, response);
            return;
        }
        DispatchHelper.dispatchWebInf(request, response, AccountPage.DETAIL);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = getSessionAttr(request, Env.SessionAttr.ACCOUNT);
        if (account == null) {
            dispatchLogin(request, response);
            return;
        }
        setRequestAttr(request, RequestAttr.ACTION, Action.EDIT);
        setRequestAttr(request, RequestAttr.EDITABLE_ACCOUNT, new EditableAccount().copyFrom(account));
        DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
    }

    private void showSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        removeSessionAttr(request, Env.SessionAttr.ACCOUNT);
        setRequestAttr(request, RequestAttr.ACTION, Action.SIGNUP);
        setRequestAttr(request, RequestAttr.EDITABLE_ACCOUNT, new EditableAccount());
        DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
    }

    private void dispatchLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DispatchHelper.dispatchWebInf(request, response, AccountPage.LOGIN);
    }

    //-- Constants --
    private class AccountPage {

        public static final String LOGIN = "account/AccountLogin.jsp";
        public static final String EDIT = "account/AccountEdit.jsp";
        public static final String DETAIL = "account/AccountDetail.jsp";
        public static final String WELCOME_REGISTERED = "account/AccountWelcomeRegistered.jsp";
    }

    private class RequestAttr {
        public final static String EDITABLE_ACCOUNT = "editableAccount";
        public static final String ACTION = "action";
        public static final String INVALID_CREDENTIALS = "invalid_credentials";
        public static final String ERRORS = "errors";
    }

    private class RequestParam {
        public static final String EMAIL = "email";
        public static final String REPEATED_EMAIL = "repeatedEmail";
        public static final String PASSWORD = "password";
        public static final String REPEATED_PASSWORD = "repeatedPassword";
        public static final String CURRENT_PASSWORD = "currentPassword";
        public static final String FIRST_NAME = "firstName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String LAST_NAME = "lastName";
    }

    private class ActionPath {
        public static final String LOGIN = "/account/login";
        public static final String LOGOUT = "/account/logout";
        public static final String EDIT = "/account/edit";
        public static final String DETAIL = "/account/detail";
        public static final String SIGNUP = "/account/signup";
    }

    private class Action {
        public static final String LOGIN = "login";
        public static final String LOGOUT = "logout";
        public static final String EDIT = "edit";
        public static final String SIGNUP = "signup";
        public static final String VIEW = "view";
    }

    private class ContextAttrValue {

        public static final String INVALID_CREDENTIALS = "invalid_credentials";
    }

    public class EditableAccount extends Account {
        private String repeatedEmail;
        private String currentPassword;
        private String newPassword;
        private String repeatedPassword;

        public EditableAccount copyFrom(Account account) {
            setFirstName(account.getFirstName());
            setMiddleName(account.getMiddleName());
            setLastName(account.getLastName());
            setEmail(account.getEmail());
            setCreatedOn(account.getCreatedOn());
            setUpdatedOn(account.getUpdatedOn());
            return this;
        }

        public String getRepeatedEmail() {
            return repeatedEmail;
        }

        public void setRepeatedEmail(String repeatedEmail) {
            this.repeatedEmail = repeatedEmail;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getRepeatedPassword() {
            return repeatedPassword;
        }

        public void setRepeatedPassword(String repeatedPassword) {
            this.repeatedPassword = repeatedPassword;
        }

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }
    }
}
