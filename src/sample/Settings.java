package sample;

import com.google.gson.InstanceCreator;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class Settings {
    InstanceCreator<PrintService> printServiceInstanceCreator = type -> PrintServiceLookup.lookupDefaultPrintService();
    public String mail = "";
    public String passwd = "";
    public String host = "";
    public int port = 993;
    public int retries = 3;

    public Settings() {

    }

    public Settings(String mail, String passwd, int port, String host) {
        this.mail = mail;
        this.passwd = passwd;
        this.port = port;
        this.host = host;
    }

    public Settings(String mail, String passwd, int port, String host, InstanceCreator<PrintService> printServiceInstanceCreator) {
        this(mail, passwd, port, host);
        this.printServiceInstanceCreator = printServiceInstanceCreator;
    }
}
