//This file is part of JPACS.
//
//    JPACS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    JPACS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JPACS.  If not, see <http://www.gnu.org/licenses/>.

package br.pucminas.dcc.jpacs.gui;

import javax.swing.text.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * Classe extraida do programa Coffe Maker e adaptado para o simulador de cache.
 * Essa classe contem o código responsavel pela syntax highlight do codigo fonte.
 * 
 * @author Guilherme Hermetto de Pádua
 */
class CommandStyledDocument extends DefaultStyledDocument {

    private StringBuffer[] JavaArray;
    private Style javacom,  normaltxt,  function,  comment;
    boolean multiline;
    private StringAnalizer txta;

    CommandStyledDocument() {
        super();
        getJavaCommands();
        getStyles();
        multiline = false;
        try {
            txta = new StringAnalizer(getText(0, getLength()), "\n", false);
        } catch (Exception excep) {
        }
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
        StringAnalizer sta;
        String currentLine;
        int startOfLine, endOfLine, endOfString, times, lineCount = 0;
        int[] offsLines;
        String[] allLines;
        try {
            writeLock();
            startOfLine = getText(0, offs).lastIndexOf("\n") + 1;
            endOfString = getEndPosition().getOffset();
            endOfLine = getText(0, endOfString).indexOf("\n", startOfLine);
            if (str.length() > (endOfLine - startOfLine)) {
                endOfLine = startOfLine + str.length();
            }
            currentLine = getText(0, endOfString).substring(startOfLine, endOfLine);
            sta = new StringAnalizer(currentLine, " ;.{}[])(,\n<>=\t", true);
            txta = new StringAnalizer(getText(0, getLength()), "\n", false);
            writeUnlock();
            setCharacterAttributes(startOfLine, currentLine.length(), normaltxt, true);
            for (int i = 0; i < sta.getWordCount(); i++) {
                for (int j = 0; j < JavaArray.length; j++) {
                    StringBuffer tempBuffer = JavaArray[j];
                    String tempString = tempBuffer.toString();
                    int gsp = sta.getStartPosition(i);
                    int gep = sta.getWord(i).length();
                    int gefp = sta.getWord(i).indexOf("(");
                    if (sta.getWord(i).indexOf(tempString.trim()) != -1 && sta.getWord(i).length() == tempString.trim().length()) {
                        setCharacterAttributes(startOfLine + gsp, gep, javacom, true);
                        sta.style[i] = 'j';
                        break;
                    } else if (sta.getWord(i - 1).length() == 1 && sta.style[i - 1] != 'j' && Character.isLetter(sta.getWord(i - 1).charAt(0))) {

                        setCharacterAttributes(startOfLine + sta.getStartPosition(i - 1), sta.getWord(i - 1).length(), function, true);
                        sta.style[i] = 'f';
                        break;
                    }

                }
            }
            writeLock();
        } catch (Exception e) {
        } finally {
            writeUnlock();
        }

    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        writeLock();
        txta = new StringAnalizer(getText(0, getLength()), "\n", false);
        writeUnlock();
    }

    protected void getJavaCommands() {
        StringTokenizer stoken;
        try {
            DefaultStyledDocument dcc = new DefaultStyledDocument();
            //File filex = new File("assembly.syn");
            //Reader javasyn = new FileReader(filex);
            Reader javasyn = new InputStreamReader(this.getClass().getResourceAsStream("/br/pucminas/dcc/jpacs/resources/assembly.syn"));
            char[] buff = new char[4096];
            int nch, ntokens;
            while ((nch = javasyn.read(buff, 0, buff.length)) != -1) {
                dcc.insertString(dcc.getLength(), new String(buff, 0, nch), null);
            }
            stoken = new StringTokenizer(dcc.getText(0, dcc.getLength()), "\n");
            ntokens = stoken.countTokens();
            JavaArray = (StringBuffer[]) Array.newInstance(StringBuffer.class, ntokens);
            for (int i = 0; stoken.hasMoreTokens(); i++) {
                JavaArray[i] = new StringBuffer(stoken.nextToken());
            }
        } catch (FileNotFoundException FNFE) {
            System.out.print("File not Found. ");
        } catch (BadLocationException BLE) {
        } catch (IOException IOE) {
        } catch (Exception e) {
            System.out.print("\n ERRO: " + e);
        }
    }

    protected void getStyles() {
        javacom = addStyle("styleJavaCom", null);
        StyleConstants.setForeground(javacom, Color.black);
        StyleConstants.setBold(javacom, true);

        normaltxt = addStyle("styleNormal", null);
        StyleConstants.setForeground(normaltxt, Color.black);
        StyleConstants.setBold(normaltxt, false);

        function = addStyle("styleFunction", null);
        StyleConstants.setForeground(function, Color.blue);
        StyleConstants.setBold(function, false);

        comment = addStyle("styleComment", null);
        StyleConstants.setForeground(comment, new Color(0, 150, 0));
        StyleConstants.setBold(comment, false);
    }

    public int getLineCount() {
        return txta.getWordCount();
    }
}

class StringAnalizer {

    protected String sentence;
    private int[] startPosition;
    private StringBuffer[] word;
    protected StringTokenizer stk;
    private int wordCount;
    public char[] style;

    StringAnalizer(String s, String sep, boolean retsep) {
        sentence = s;
        stk = new StringTokenizer(sentence, sep, retsep);
        wordCount = stk.countTokens();
        startPosition = (int[]) Array.newInstance(int.class, wordCount);
        word = (StringBuffer[]) Array.newInstance(StringBuffer.class, wordCount);
        style = (char[]) Array.newInstance(char.class, wordCount);
        int i, cab = 0;
        for (i = 0; stk.hasMoreTokens(); i++) {
            String taword = stk.nextToken();
            word[i] = new StringBuffer(taword);
            startPosition[i] = sentence.indexOf(taword, cab);
            cab = taword.length() + startPosition[i];
        }
    }

    public String getWord(int index) {
        String finalWord = "";
        StringBuffer tempWord;
        try {
            tempWord = word[index];
            finalWord = tempWord.toString();
        } catch (Exception e) {
            //System.out.print(" Exception: There is no word in this index.");
        } finally {
            return finalWord;
        }
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getStartPosition(int index) {
        int tempInt = 0;
        try {
            tempInt = startPosition[index];
        } catch (Exception e) {
            //System.out.print(" There is no word in this index.");
        } finally {
            return tempInt;
        }
    }

    public StringBuffer getWordBuffer(int index) {
        StringBuffer tempWord = new StringBuffer();
        try {
            tempWord = word[index];
        } catch (Exception e) {
            //System.out.print(" There is no word in this index.");
        } finally {
            return tempWord;
        }
    }
}

