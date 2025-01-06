package com.manager.class_activity.qnu.constant;

public class EmailTemplateConstant {

    public static String generateNotificationTemplate(String name, String time) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Study Notification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f9fc;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .email-header {\n" +
                "            background-color: #0078d7;\n" +
                "            color: #ffffff;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .email-header img {\n" +
                "            max-width: 100px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .email-header h1 {\n" +
                "            margin: 0;\n" +
                "            font-weight: bold;\n" +
                "            font-size: 24px;\n" +
                "            color: white;\n" +
                "            line-height: 100%;\n" +
                "        }\n" +
                "        .email-body {\n" +
                "            padding: 20px;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .email-body p {\n" +
                "            line-height: 1.6;\n" +
                "            margin: 10px 0;\n" +
                "        }\n" +
                "        .email-body .highlight {\n" +
                "            color: #0078d7;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .email-footer {\n" +
                "            background-color: #f4f9fc;\n" +
                "            text-align: center;\n" +
                "            padding: 15px;\n" +
                "            font-size: 14px;\n" +
                "            color: #666666;\n" +
                "        }\n" +
                "        .email-footer a {\n" +
                "            color: #0078d7;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .btn {\n" +
                "            display: inline-block;\n" +
                "            background-color: #0078d7;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 15px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .btn:hover {\n" +
                "            background-color: #005bb5;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"email-header\">\n" +
                "            <img src=\"https://qnu.edu.vn/Resources/Images/0logoDHQNnew.jpg\" alt=\"University Logo\">\n" +
                "            <h1>Study Notification</h1>\n" +
                "        </div>\n" +
                "        <div class=\"email-body\">\n" +
                "            <p>Dear <span class=\"highlight\">" + name + "</span>,</p>\n" +
                "            <p>We would like to inform you about your upcoming study schedule:</p>\n" +
                "            <p><strong>Date and Time:</strong> " + time + "<br>\n" +
                "            <strong>Location:</strong> Room 101, Building A, Quy Nhon University</p>\n" +
                "            <p>Please make sure to arrive on time and bring all necessary materials.</p>\n" +
                "            <a href=\"#\" class=\"btn\">View Details</a>\n" +
                "            <p>If you have any questions, feel free to contact us via email or our support phone number.</p>\n" +
                "        </div>\n" +
                "        <div class=\"email-footer\">\n" +
                "            <p>Best regards,<br>\n" +
                "            Academic Affairs Office, Quy Nhon University</p>\n" +
                "            <p><a href=\"mailto:support@abc.edu\">support@abc.edu</a> | Phone: 0123 456 789</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
