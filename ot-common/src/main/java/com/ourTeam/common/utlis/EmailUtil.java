package com.ourTeam.common.utlis;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

public class EmailUtil {

    /**
     * 发件人的邮箱和密码
     * 某些邮箱服务器为了增加邮箱本身密码的安全性
     * 给SMTP,客户端设置了独立密码（有的邮箱称为“授权码”）
     * 比如163，这里我用163邮箱测试的，
     * 设置了独立密码的邮箱,所以这里的邮箱密码必需使用这个独立密码
     **/
    public static String myEmailAccount = "513533693@qq.com";
    public static String myEmailPassword = "xgbuagvzzmxicacj";
    /**
     *  发件人邮箱的 SMTP 服务器地址必须准确, 不同邮件服务器地址不同,
     *  网易163邮箱的 SMTP 服务器地址为: smtp.163.com
     */
    public static String myEmailSMTPHost = "smtp.qq.com";


    /**
     * 使用别人家的邮箱服务器，例如163邮箱，qq邮箱等
     * @param to 给谁发邮件，也就是发送邮件的地址
     * @param code 激活码
     */
    public static void sendEmailByWeb(String to,String code) throws Exception {

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.setProperty("mail.smtp.socketFactory.fallback", "false");
	    props.setProperty("mail.smtp.socketFactory.port", "465");

        //2.创建链接对象，链接到邮箱服务器
        Session session = Session.getDefaultInstance(props);
        //这里是为了在控制台可以看到详细发送的信息
        session.setDebug(true);

        //3.创建邮件对象
        MimeMessage message = createMimeMessage(session,myEmailAccount,to,code);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        //使用邮箱账号和密码连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        transport.connect(myEmailAccount, myEmailPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();

    }


    /**
     * 使用自己本地的邮箱服务器，这里我用的是易邮邮箱服务器
     * @param to
     * @param code
     * @throws Exception
     */
    public static void sendEmailByLocal(String to,String code) throws Exception {

        //1、连接配置，本地不用配置
        Properties props = new Properties();
        //2、创建连接对象
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //这里是本地的邮箱账户和密码，域名可以在邮箱服务器自己设定
                return new PasswordAuthentication("server@sinnamm.con","server");
            }
        });
        //获取邮件对象
        Message message = createMimeMessage(session,"server@sinnamm.com",to,code);
        //发送邮件
        Transport.send(message);
    }

    /**
     * 创建邮件
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String code) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件
        message.setFrom(new InternetAddress(sendMail, "findlover", "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "用户", "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("激活邮件", "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent("<h1>请点击以下链接激活你的账号</h1>" +
                "<h3><a href='http://localhost:8082/ActiveServlet?code="+code+"'>http://localhost:8082/ActiveServlet?code="+code+"</a></h3>","text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }
}
