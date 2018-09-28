package io.nuls.contract.idea.plugin.model;

public class NulsAccount implements TreeItem {
    /**
     * 账户地址
     */
    private String address;

    /**
     * 账户别名
     */
    private String alias;

    /**
     * 密码
     */
    private String password;

    /**
     * 公钥
     */
    private String pubKey;

    /**
     * 私钥
     */
    private String prikey;

    /**
     * 加密后的私钥
     */
    private String encryptedPrivateKey;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public static NulsAccount byDefault() {
        NulsAccount account = new NulsAccount();
        account.setAddress("the address");
        return account;
    }

    public String toString() {
        return this.address;
    }
}
