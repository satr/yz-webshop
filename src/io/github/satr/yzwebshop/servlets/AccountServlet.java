package io.github.satr.yzwebshop.servlets;

import io.github.satr.yzwebshop.entities.Account;
import io.github.satr.yzwebshop.helpers.DispatchHelper;
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
        removeContextAttr(request, ContextAttr.ACTION);
        switch(request.getServletPath()) {
            case ActionPath.DETAIL:
                showDetail(request, response);
                break;
            case ActionPath.LOGIN:
                removeContextAttr(request, ContextAttr.INVALID_CREDENTIALS);
                showLogin(request, response);
                break;
            case ActionPath.LOGOUT:
                removeContextAttr(request, ContextAttr.ACCOUNT);
                removeContextAttr(request, ContextAttr.ACTION);
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

        if(!validateParams(request, response, email, password, errorMessages)
           ||  !authenticate(request, response, email, password))
            return;

        String action = getContextAttr(request, ContextAttr.ACTION);
        if (action == null || action.equals(Action.LOGIN))
            DispatchHelper.dispatchHome(request, response);
        else if (action.equals(Action.VIEW))
            showDetail(request, response);
        else if (action.equals(Action.EDIT))
            showEdit(request, response);
        else
            DispatchHelper.dispatchError(request, response, "Undefined action \"%s\"", action == null ? "Empty" : action);
    }

    private void processSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = getContextAttr(request, ContextAttr.ACCOUNT);
        if(account == null || getContextAttr(request, ContextAttr.ACTION) != Action.SIGNUP) {
            showSignUp(request, response);
            return;
        }

        ArrayList<String> errorMessages = new ArrayList<>();
        account.setEmail(ParameterHelper.getString(request, RequestParam.EMAIL, errorMessages));
        String repeatEmail = ParameterHelper.getString(request, RequestParam.REPEAT_EMAIL, errorMessages);
        String password = ParameterHelper.getString(request, RequestParam.PASSWORD, errorMessages);
        String repeatPassword = ParameterHelper.getString(request, RequestParam.REPEAT_PASSWORD, errorMessages);
        account.setFirstName(ParameterHelper.getString(request, RequestParam.FIRST_NAME, errorMessages));
        account.setMiddleName(ParameterHelper.getString(request, RequestParam.MIDDLE_NAME, errorMessages));
        account.setLastName(ParameterHelper.getString(request, RequestParam.LAST_NAME, errorMessages));

        removeContextAttr(request, ContextAttr.ACCOUNT_PARAMS_ISSUES);

        if (!validateSignUpParams(account, repeatEmail, password, repeatPassword, errorMessages)) {
            setContextAttr(request, ContextAttr.ACCOUNT_PARAMS_ISSUES, errorMessages);
            DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
            return;
        }

        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            account.setCreatedOn(timestamp);
            account.setUpdatedOn(timestamp);
            account.setPasswordSalt("" + random.nextLong());
            setAccountPassword(account, password);
            accountRepository.save(account);
        } catch (Exception e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return;
        }

        removeContextAttr(request, ContextAttr.ACTION);
        DispatchHelper.dispatchWebInf(request, response, AccountPage.WELCOME_REGISTERED);
    }

    private void setAccountPassword(Account account, String password) throws NoSuchAlgorithmException {
        account.setPasswordHash(getHashBy(password, account.getPasswordSalt()));
    }

    private boolean validateSignUpParams(Account account, String repeatEmail, String password, String repeatPassword, ArrayList<String> errorMessages) throws ServletException, IOException {
        validateNewNames(account.getFirstName(), account.getFirstName(), errorMessages);
        validateNewEmail(account.getEmail(), repeatEmail, errorMessages);
        validateNewPassword(password, repeatPassword, errorMessages);
        return errorMessages.size() == 0;
    }

    private boolean validateEditParams(Account account, String email, String repeatEmail, String firstName, String lastName, String currentPassword, String newPassword,
                                       String repeatedPassword, ArrayList<String> errorMessages) throws ServletException, IOException {
        validateNewNames(firstName, lastName, errorMessages);
        if(account.getEmail().compareTo(email) != 0)
            validateNewEmail(email, repeatEmail, errorMessages);
        if(!isEmptyOrWhitespace(newPassword) || !isEmptyOrWhitespace(repeatedPassword)) {
            if(isEmptyOrWhitespace(currentPassword))
                errorMessages.add("Missed Current Password.");
            else if(!validatePassword(account, currentPassword))
                errorMessages.add("Invalid Current Password.");
            validateNewPassword(newPassword, repeatedPassword, errorMessages);
        }
        return errorMessages.size() == 0;
    }

    private boolean isEmptyOrWhitespace(String value) {
        return value == null || value.trim().length() == 0;
    }

    private void validateNewNames(String firstName, String lastName, ArrayList<String> errorMessages) {
        if(isEmptyOrWhitespace(firstName))
            errorMessages.add("Missed First Name.");
        if(isEmptyOrWhitespace(lastName))
            errorMessages.add("Missed Last Name.");
    }

    private void validateNewPassword(String password, String repeatedPassword, ArrayList<String> errorMessages) {
        boolean missedPassword = isEmptyOrWhitespace(password);
        if(missedPassword)
            errorMessages.add("Missed Password.");
        boolean missedRepeatedPassword = isEmptyOrWhitespace(repeatedPassword);
        if(missedRepeatedPassword)
            errorMessages.add("Missed Repeated Password.");
        if(missedPassword || missedRepeatedPassword)
            return;
        if(password.compareTo(repeatedPassword) != 0)
            errorMessages.add("Password and Repeated Password do not match.");
    }

    private void validateNewEmail(String email, String repeatEmail, ArrayList<String> errorMessages) {
        boolean missedEmail = email == null || email.length() == 0;
        if(missedEmail)
            errorMessages.add("Missed Email.");
        boolean missedRepeatedEmail = repeatEmail == null || repeatEmail.length() == 0;
        if(missedRepeatedEmail)
            errorMessages.add("Missed Repeated Email.");
        if (missedEmail || missedRepeatedEmail)
            return;
        if(email.compareTo(repeatEmail) != 0)
            errorMessages.add("Email and Repeated Email do not match.");
        else if( checkEmailAlreadyRegistered(email, errorMessages))
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

    private boolean validateParams(HttpServletRequest request, HttpServletResponse response, String email, String password, List<String> errorMessages) throws ServletException, IOException {
        if(email == null || email.length() == 0)
            errorMessages.add("Email should not be empty.");

        if(password == null || password.length() == 0)
            errorMessages.add("Password should not be empty.");

        if (errorMessages.size() > 0) {
            setContextAttr(request, ContextAttr.INVALID_CREDENTIALS, ContextAttrValue.INVALID_CREDENTIALS);
            showLogin(request, response);
            return false;
        }
        return true;
    }

    private boolean authenticate(HttpServletRequest request, HttpServletResponse response, String email, String password) throws ServletException, IOException {
        Account account = null;
        try {
            account = accountRepository.getByEmail(email);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return false;
        }

        if(account == null || !validatePassword(account, password)) {
            setContextAttr(request, ContextAttr.INVALID_CREDENTIALS, ContextAttrValue.INVALID_CREDENTIALS);
            showLogin(request, response);
            return false;
        }

        setContextAttr(request, ContextAttr.ACCOUNT, account);
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

    private void processEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = getContextAttr(request, ContextAttr.ACCOUNT);
        if(account == null || getContextAttr(request, ContextAttr.ACTION) != Action.EDIT) {
            showEdit(request, response);
            return;
        }

        ArrayList<String> errorMessages = new ArrayList<>();
        String email = ParameterHelper.getString(request, RequestParam.EMAIL, errorMessages);
        String repeatEmail = ParameterHelper.getString(request, RequestParam.REPEAT_EMAIL, errorMessages);
        String currentPassword = ParameterHelper.getString(request, RequestParam.CURRENT_PASSWORD, errorMessages);
        String newPassword = ParameterHelper.getString(request, RequestParam.PASSWORD, errorMessages);
        String repeatedPassword = ParameterHelper.getString(request, RequestParam.REPEAT_PASSWORD, errorMessages);
        String firstName = ParameterHelper.getString(request, RequestParam.FIRST_NAME, errorMessages);
        String middleName = ParameterHelper.getString(request, RequestParam.MIDDLE_NAME, errorMessages);
        String lastName = ParameterHelper.getString(request, RequestParam.LAST_NAME, errorMessages);

        removeContextAttr(request, ContextAttr.ACCOUNT_PARAMS_ISSUES);

        if (!validateEditParams(account, email, repeatEmail, firstName, lastName, currentPassword, newPassword, repeatedPassword, errorMessages)) {
            setContextAttr(request, ContextAttr.ACCOUNT_PARAMS_ISSUES, errorMessages);
            DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
            return;
        }

        try {
            account.setFirstName(firstName);
            account.setMiddleName(middleName);
            account.setLastName(lastName);
            if(shouldChangePassword(account, currentPassword, newPassword, repeatedPassword))
                setAccountPassword(account, newPassword);
            account.setEmail(email);
            Timestamp timestamp = new Timestamp(new Date().getTime());
            account.setUpdatedOn(timestamp);
            accountRepository.save(account);
        } catch (Exception e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());//TODO -  wrap with user-friendly message and log
            return;
        }

        removeContextAttr(request, ContextAttr.ACTION);
        DispatchHelper.dispatchWebInf(request, response, AccountPage.WELCOME_REGISTERED);
    }

    private boolean shouldChangePassword(Account account, String currentPassword, String newPassword, String repeatedPassword) {
        return !isEmptyOrWhitespace(newPassword) && !isEmptyOrWhitespace(repeatedPassword)
                && newPassword.compareTo(repeatedPassword) == 0
                && currentPassword.compareTo(newPassword) != 0
                && validatePassword(account, currentPassword);
    }

    private void removeContextAttr(HttpServletRequest request, String attrName) {
        request.getServletContext().removeAttribute(attrName);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setContextAttr(request, ContextAttr.ACTION, Action.VIEW);
        if (!validateSession(request, response)) {
            showLogin(request, response);
            return;
        }
        DispatchHelper.dispatchWebInf(request, response, AccountPage.DETAIL);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setContextAttr(request, ContextAttr.ACTION, Action.EDIT);
        if (!validateSession(request, response)) {
            showLogin(request, response);
            return;
        }
        DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
    }

    private void showSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        removeContextAttr(request, ContextAttr.ACCOUNT_PARAMS_ISSUES);
        removeContextAttr(request, ContextAttr.ACCOUNT);
        setContextAttr(request, ContextAttr.ACTION, Action.SIGNUP);
        setContextAttr(request, ContextAttr.ACCOUNT, new Account());
        DispatchHelper.dispatchWebInf(request, response, AccountPage.EDIT);
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DispatchHelper.dispatchWebInf(request, response, AccountPage.LOGIN);
    }

    private boolean validateSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = getContextAttr(request, ContextAttr.ACCOUNT);
        return account != null;
    }

    private <T> T getContextAttr(HttpServletRequest request, String attrName) {
        return (T) request.getServletContext().getAttribute(attrName);
    }

    private void setContextAttr(HttpServletRequest request, String attrName, Object value) {
        request.getServletContext().setAttribute(attrName, value);
    }

    //-- Constants --
    private class AccountPage {

        public static final String LOGIN = "account/AccountLogin.jsp";
        public static final String EDIT = "account/AccountEdit.jsp";
        public static final String DETAIL = "account/AccountDetail.jsp";
        public static final String WELCOME_REGISTERED = "account/AccountWelcomeRegistered.jsp";
    }

    private class ContextAttr {
        public final static String ACCOUNT = "account";
        public static final String ACTION = "action";
        public static final String INVALID_CREDENTIALS = "invalid_credentials";
        public static final String ACCOUNT_PARAMS_ISSUES = "account_params_issues";
    }

    private class RequestParam {
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String REPEAT_EMAIL = "repeatEmail";
        public static final String PASSWORD = "password";
        public static final String REPEAT_PASSWORD = "repeatPassword";
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
}
