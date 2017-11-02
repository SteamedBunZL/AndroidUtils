package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * @author zengtao.kuang
 * @Description: ProcAccountFilter的辅助类
 * @date 2016/4/6 15:50
 * @copyright TCL-MIG
 */
public class AccountScanner {
    public static final int ACCOUNT_LOGOUT = 0;
    public static final int ACCOUNT_LOGIN = 1;
    public static final int NO_ACCOUNT_PKG = 2;

    private final String[] mAccountTypes = {
            "com.osp.app.signin",
            "com.sec.chaton",
            "com.facebook.auth.login",
            "com.dropbox.android.account",
            "com.twitter.android.auth.login",
            "com.tencent.mm.account",
            "com.viber.voip.account",
            "com.skype.contacts.sync"
    };
    private final String[][] mAccountTypePkgs = {
            {"com.sec.chaton", "com.sec.android.app.samsungapps", "com.osp.app.signin"},
            {"com.sec.chaton"},
            {"com.facebook.katana", "com.facebook.orca"},
            {"com.dropbox.android"},
            {"com.twitter.android"},
            {"com.tencent.mm"},
            {"com.viber.voip"},
            {"com.skype.raider", "com.skype.rover", "com.skype.android.verizon", "com.skype.android.kddi", "com.skype.android.threeAU", "com.skype.android.vodafoneAU"}
    };

    private boolean[] mAccountTypeEnabled = {false, false, false, false, false, false, false, false};

    public AccountScanner(Context ctx) {
        Account[] accounts = null;

        try {
            AccountManager acm = AccountManager.get(ctx);
            accounts = acm.getAccounts();
        } catch (Exception e) {
            // workaround for no permission security exception
        }

        if (accounts != null) {
            for (int i = 0; i < accounts.length; i++) {
                for (int typeIndex = 0; typeIndex < mAccountTypes.length; typeIndex++) {
                    if (accounts[i].type.equals(mAccountTypes[typeIndex])) {
                        mAccountTypeEnabled[typeIndex] = true;
                        break;
                    }
                }
            }
        } else {
            // we can't get account information, so we assume all account enabled
            for (int i = 0; i < mAccountTypeEnabled.length; i++) {
                mAccountTypeEnabled[i] = true;
            }
        }
    }

    public int getPackageAccountStatus(String pkgName) {
        for (int typeIndex = 0; typeIndex < mAccountTypes.length; typeIndex++) {
            for (int pkgIndex = 0; pkgIndex < mAccountTypePkgs[typeIndex].length; pkgIndex++) {
                if (pkgName.equals(mAccountTypePkgs[typeIndex][pkgIndex])) {
                    if (mAccountTypeEnabled[typeIndex] == true) {
                        return ACCOUNT_LOGIN;
                    } else {
                        return ACCOUNT_LOGOUT;
                    }
                }
            }
        }
        return NO_ACCOUNT_PKG;
    }
}
