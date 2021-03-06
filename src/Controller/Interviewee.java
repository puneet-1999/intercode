package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import intercode.Compile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

/**
 *
 * @author TARUN
 */
public class Interviewee implements Initializable {

    static ServerSocket ss = null;
    static Socket s;
    static DataInputStream dis = null;
    static DataOutputStream dos = null;
    Compile compiler = new Compile();
    @FXML
    private Label C;

    @FXML
    private Label CPP;

    @FXML
    private Label JAVA;

    @FXML
    private Label PYTHON;

    @FXML
    private JFXComboBox<?> frontsize;

    @FXML
    private JFXComboBox<?> fronttype;

    @FXML
    private JFXTextArea input;

    @FXML
    private JFXTextArea output;

    @FXML
    private JFXButton compile;

    @FXML
    private JFXTextArea question;

    @FXML
    private JFXButton endinter;

    @FXML
    private CodeArea editor;
    private static final String[] KEYWORDSJAVA = new String[]{"abstract", "assert", "boolean", "break",
        "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private static final String[] KEYWORDSCPP = {"auto", "const", "double", "float", "int", "short",
        "struct", "unsigned", "break", "continue", "else", "for", "long", "signed",
        "switch", "void", "case", "default", "enum", "goto", "register", "sizeof",
        "typedef", "volatile", "char", "do", "extern", "if", "return", "static",
        "union", "while", "asm", "dynamic_cast", "namespace", "reinterpret_cast", "try",
        "bool", "explicit", "new", "static_cast", "typeid", "catch", "false", "operator",
        "template", "typename", "class", "friend", "private", "this", "using", "const_cast",
        "inline", "public", "throw", "virtual", "delete", "mutable", "protected", "true", "wchar_t"};

    private static final String[] KEYWORDSC = {"auto", "break", "case", "char", "const", "continue", "default",
        "do", "double", "else", "enum", "extern", "float", "for", "goto", "if", "int", "long", "register",
        "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef", "union",
        "unsigned", "void", "volatile", "while"
    };

    private static final String[] KEYWORDSPYTHON = {"False", "class", "finally", "is", "return", "None", "continue",
        "for", "lambda", "try", "True", "def", "from", "nonlocal", "while", "and", "del", "global", "not", "with", "as",
        "elif", "if", "or", "yield", "assert", "else", "import", "pass", "break", "except", "in", "raise"
    };

    private static final String KEYWORD_PATTERN_JAVA = "\\b(" + String.join("|", KEYWORDSJAVA) + ")\\b";
    private static final String KEYWORD_PATTERN_CPP = "\\b(" + String.join("|", KEYWORDSCPP) + ")\\b";
    private static final String KEYWORD_PATTERN_C = "\\b(" + String.join("|", KEYWORDSC) + ")\\b";
    private static final String KEYWORD_PATTERN_PYTHON = "\\b(" + String.join("|", KEYWORDSPYTHON) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN_JAVA = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_JAVA + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_CPP = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_CPP + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_C = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_C + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_PYTHON = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_PYTHON + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    Task task = new Task<Void>() {
        @Override
        public Void call() throws Exception {
            int i = 0;
            while (true) {
                String msg = dis.readUTF();
                System.out.println("read message= " + msg);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        editor.replaceText(msg);
                    }
                });
                Thread.sleep(100);
            }
        }
    };

    @FXML
    void changesize(ActionEvent event) {

    }

    @FXML
    void changetype(ActionEvent event) {

    }

    @FXML
    void endinterview(ActionEvent event) throws IOException {
        ss.close();
        dis.close();
        dos.close();
        Parent root = FXMLLoader.load(getClass().getResource("UI.Login.fxml"));
        Scene scene = new Scene(root);
        Stage stg = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stg.hide();
        stg.setScene(scene);
        stg.show();

    }
    static int language = 1;

    @FXML
    void onclickC(MouseEvent event) {
        String c = "#include<stdio.h>" + System.lineSeparator() + "int main(void){" + System.lineSeparator() + "    //Code" + System.lineSeparator() + "    Return 0;" + System.lineSeparator() + "}";
        editor.replaceText(c);
        language = 1;
        CPP.setTextFill(Color.web("#000000"));
        JAVA.setTextFill(Color.web("#000000"));
        C.setTextFill(Color.web("#ff0000"));
        if (editorStatus == 0) {
            System.out.println("starting reading thread");
//            readMessage.start();
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            editorStatus = 1;
        }
    }
    private static int editorStatus = 0;

    @FXML
    void onclickCPP(MouseEvent event) {
        String c = "#include <iostream>" + System.lineSeparator() + "using namespace std;" + System.lineSeparator() + "int main(void){" + System.lineSeparator() + "    //Code" + System.lineSeparator() + "    Return 0;" + System.lineSeparator() + "}";
        editor.replaceText(c);
        language = 2;
        CPP.setTextFill(Color.web("#ff0000"));
        C.setTextFill(Color.web("#000000"));
        JAVA.setTextFill(Color.web("#000000"));
        if (editorStatus == 0) {
            System.out.println("starting reading thread");
//            readMessage.start();
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            editorStatus = 1;
        }

    }

    @FXML
    void onclickJAVA(MouseEvent event) {
        String c = "import java.io.*" + System.lineSeparator() + "class Gochi {" + System.lineSeparator() + "    public static void main (String[] args) {" + System.lineSeparator() + "        //code;" + System.lineSeparator() + "	}" + System.lineSeparator() + "}";
        language = 3;
        editor.replaceText(c);
        JAVA.setTextFill(Color.web("#ff0000"));
        C.setTextFill(Color.web("#000000"));
        CPP.setTextFill(Color.web("#000000"));
        if (editorStatus == 0) {
            System.out.println("starting reading thread");
//            readMessage.start();
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            editorStatus = 1;
        }
    }

    @FXML
    void onclickPYTHON(MouseEvent event) {

    }

    void setInput() throws IOException {
        String input1 = input.getText();
        String input = new String(input1 + " ");
        if (input == null) {
            return;
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:\\Users\\TARUN\\Documents\\NetBeansProjects\\InterCode\\src\\Judge\\input.txt"), "utf-8"))) {
            System.out.println(input);
            writer.write(input);
        }
    }

    @FXML
    void oncompile(ActionEvent event) throws IOException, InterruptedException {
        boolean success = false;
        setInput();
        if (language == 1) {
            success = compiler.compiling("C");
        } else if (language == 2) {
            success = compiler.compiling("CPP");
        } else if (language == 3) {
            success = compiler.compiling("JAVA");
        }
        Thread.sleep(500);
        if (success) {
            String error = "";
            String output1 = "";
            //reading output
            try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\TARUN\\Documents\\NetBeansProjects\\InterCode\\src\\Judge\\output.txt"))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                output1 = sb.toString();
                output.setText(output1);
            }
            if (output.equals("")) {
                try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\TARUN\\Documents\\NetBeansProjects\\InterCode\\src\\Judge\\error.txt"))) {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    error = sb.toString();
                    output.setText(error);
                }
            } else {
            }
        } else {
            output.setText("Compilation Error");
        }
    }

    @FXML
    void onwriting(KeyEvent event) {
        try {
            dos.writeUTF(editor.getText());
            dos.flush();
            System.out.println("send message: " + editor.getText());
        } catch (IOException ex) {
//                Logger.getLogger(layoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
        Subscription cleanupWhenNoLongerNeedIt = editor.multiPlainChanges().successionEnds(Duration.ofMillis(500)).subscribe(ignore -> editor.setStyleSpans(0, computeHighlighting(editor.getText())));
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN_C.matcher(text);;
        if (language == 2) {
            matcher = PATTERN_CPP.matcher(text);
        } else if (language == 3) {
            matcher = PATTERN_JAVA.matcher(text);
        } else if (language == 4) {
            matcher = PATTERN_PYTHON.matcher(text);
        }
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
