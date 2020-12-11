package sample.Connection;

public class ConnectionSettings {
    public final String mail;
    public final String passwd;
    public int port;
    public final String host;
    public int retries = 3;

    public ConnectionSettings(String mail, String passwd, int port, String host) {
        this.mail = mail;
        this.passwd = passwd;
        this.port = port;
        this.host = host;
    }

    public ConnectionSettings(String mail, String passwd, int port, String host, int retries) {
        this(mail, passwd, port, host);
        this.retries = retries;
    }

    public String getMail() {
        return mail;
    }

    public String getPasswd() {
        return passwd;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return "ConnectionSettings{" + "mail='" + mail + '\'' + ", passwd='" + passwd + '\'' + ", port=" + port
                + ", host='" + host + '\'' + '}';
    }
}
