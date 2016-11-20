package io.github.satr.yzwebshop.repositories;

import io.github.satr.yzwebshop.entities.Account;

import java.sql.SQLException;
import java.util.List;

public class AccountRepository extends HibernateRepositoryBase<Account> {
    @Override
    protected Class getEntityClass() {
        return Account.class;
    }

    @Override
    protected String getSqlForList() {
        return "from Account";
    }

    public Account getByEmail(String email) throws SQLException {
        List<Account> list = getQueryable("from Account where email = :email",
                                            query -> query.setParameter("email", email));
        return list.size() == 0 ? null : list.get(0);
    }
}
