
package com.training.auctionbidding.config;

import org.hibernate.LockOptions;
import org.hibernate.Timeout;
import org.hibernate.dialect.MySQLDialect;

public class TiDBMySQLDialect extends MySQLDialect {

    @Override
    public String getForUpdateString(String aliases) {
        return getForUpdateString();
    }

    @Override
    public String getForUpdateString(
            String aliases,
            LockOptions lockOptions) {
        return getForUpdateString(lockOptions);
    }

    @Override
    public String getWriteLockString(
            String aliases,
            Timeout timeout) {
        return getWriteLockString(timeout);
    }
}
