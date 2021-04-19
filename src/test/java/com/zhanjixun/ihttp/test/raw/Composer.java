package com.zhanjixun.ihttp.test.raw;

import okio.Okio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanjixun
 * @date 2021-04-19 10:54:57
 */
public class Composer {

    public static void main(String[] args) {
        buildUI().setVisible(true);
    }

    private static JFrame buildUI() {
        JFrame frame = new JFrame();
        frame.setTitle("Composer");
        //按Esc退出
        frame.getRootPane().registerKeyboardAction(e -> System.exit(0), "command", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(600, 580);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        //文本框字体
        Font textFont = new Font("微软雅黑", Font.PLAIN, 15);
        //文本框尺寸
        Dimension textDimension = new Dimension(frame.getWidth() - 40, (int) (frame.getHeight() * 0.4));
        //文本框内边距
        Insets textMargin = new Insets(5, 5, 5, 5);

        //今天工作描述 start
        JTextArea requestText = new JTextArea();
        requestText.setFont(textFont);
        requestText.setMargin(textMargin);
        //todayText.getDocument().addDocumentListener(new DelayDocumentListener(this::saveText));

        JScrollPane todayScrollPane = new JScrollPane(requestText);
        todayScrollPane.setPreferredSize(textDimension);
        todayScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        todayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        todayScrollPane.setBorder(new LineBorder(Color.lightGray, 1));

        JPanel todayPanel = new JPanel(new BorderLayout(10, 10));
        todayPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        todayPanel.add(new JLabel("Request"), BorderLayout.NORTH);
        todayPanel.add(todayScrollPane, BorderLayout.CENTER);
        //今天工作描述 end

        //明天工作安排 start
        JTextArea responseText = new JTextArea();
        responseText.setFont(textFont);
        responseText.setMargin(textMargin);
        //tomorrowText.getDocument().addDocumentListener(new DelayDocumentListener(this::saveText));

        JScrollPane tomorrowScrollPane = new JScrollPane(responseText);
        tomorrowScrollPane.setPreferredSize(textDimension);
        tomorrowScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tomorrowScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tomorrowScrollPane.setBorder(new LineBorder(Color.lightGray, 1));

        JPanel tomorrowPanel = new JPanel(new BorderLayout(10, 10));
        tomorrowPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
        tomorrowPanel.add(new JLabel("Response"), BorderLayout.NORTH);
        tomorrowPanel.add(tomorrowScrollPane, BorderLayout.CENTER);
        //明天工作安排 end

        //保存按钮
        JPanel savePanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Send Request");
        saveButton.addActionListener(event -> responseText.setText(sendRequest(requestText.getText())));
        savePanel.add(saveButton);

        //主面板
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(todayPanel, BorderLayout.NORTH);
        panel.add(tomorrowPanel, BorderLayout.CENTER);
        panel.add(savePanel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);

        return frame;
    }

    private static String sendRequest(String request) {
        Matcher matcher = Pattern.compile("Host: (.*)\n").matcher(request);
        String host = matcher.find() ? matcher.group(1) : "";

        int port = host.contains(":") ? Integer.parseInt(host.split(":")[1]) : 80;
        host = host.contains(":") ? host.split(":")[0] : host;

        Socket client = null;
        try {
            client = new Socket(host, port);
            OutputStream outputStream = client.getOutputStream();
            outputStream.write((request.replaceAll("\n", "\r\n")).getBytes());
            String response = Okio.buffer(Okio.source(client.getInputStream())).readUtf8();
            System.out.println(request + "---------------\n" + response);
            return response;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return null;
    }
}
