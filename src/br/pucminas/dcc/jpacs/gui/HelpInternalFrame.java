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

import br.pucminas.dcc.jpacs.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * Classe com o JInternalFrame que exibe a ajuda
 */
public class HelpInternalFrame extends JInternalFrame {

    HtmlPane Ajuda;

    public HelpInternalFrame() {
        super(Internacionalizacao.get("Menu.Help.Topics"), true, true, true, true);
        Ajuda = new HtmlPane();
        setContentPane(Ajuda);
        setBounds(200, 20, 400, 400);
        setVisible(true);
    }
};

/**
 * A parte abaixo foi extraída do arquivo MeltalworkHelp.java do diretório Demo do Jdk 1.4
 * Foram feitas leves adaptações para este programa 
 */
class HtmlPane extends JScrollPane implements HyperlinkListener {

    JEditorPane html;

    public HtmlPane() {
        try {
//	    File f = new File ("Help/index.html");
//	    String s = f.getAbsolutePath();
//	    s = "file:"+s;
            URL StartUrl = this.getClass().getResource("/br/pucminas/dcc/jpacs/help/index.html");
            html = new JEditorPane(StartUrl);
            html.setEditable(false);
            html.addHyperlinkListener(this);

            JViewport vp = getViewport();
            vp.add(html);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    /**
     * Notification of a change relative to a 
     * hyperlink.
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            linkActivated(e.getURL());
        }
    }

    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param u the URL to follow
     */
    protected void linkActivated(URL u) {
        Cursor c = html.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        html.setCursor(waitCursor);
        SwingUtilities.invokeLater(new PageLoader(u, c));
    }

    /**
     * temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {

        PageLoader(URL u, Cursor c) {
            url = u;
            cursor = c;
        }

        public void run() {
            if (url == null) {
                // restore the original cursor
                html.setCursor(cursor);

                // PENDING(prinz) remove this hack when 
                // automatic validation is activated.
                Container parent = html.getParent();
                parent.repaint();
            } else {
                Document doc = html.getDocument();
                try {
                    html.setPage(url);
                } catch (IOException ioe) {
                    html.setDocument(doc);
                    getToolkit().beep();
                } finally {
                    // schedule the cursor to revert after
                    // the paint has happended.
                    url = null;
                    SwingUtilities.invokeLater(this);
                }
            }
        }
        URL url;
        Cursor cursor;
    }
}
