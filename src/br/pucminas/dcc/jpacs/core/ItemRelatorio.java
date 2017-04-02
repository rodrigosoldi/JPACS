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

package br.pucminas.dcc.jpacs.core;

import br.pucminas.dcc.jpacs.Internacionalizacao;
/**
 * Classe para armazenar um item de Histórico de acesso
 */
public class ItemRelatorio {

    public int Numero;
    public int Posicao;
    public char Natureza; //G = Gravação, L = Leitura
    public int Bloco;
    public int Tempo;
    public char Resultado; //E = erro, A = Acerto
    public int Subs; // Se < 0, não houve substituição de bloco

    public ItemRelatorio() {
        Numero = 0;
        Posicao = 0;
        Natureza = ' ';
        Bloco = 0;
        Tempo = 0;
        Resultado = ' ';
        Subs = -1;
    }

    @Override
    public String toString() {
        String Retorno = "";

        if (Numero < 10) {
            Retorno += "0";
        }
        Retorno += "" + Numero + ": " + Posicao + "";

        if (Natureza == 'G') {
            Retorno += Internacionalizacao.get("Report.Write.Abb");
        } else {
            Retorno += Internacionalizacao.get("Report.Read.Abb");
        }
        Retorno += "  " + Internacionalizacao.get("Report.Block.Abb") + "." + Bloco + " " + Tempo + "ns ";

        if (Resultado == 'E') {
            Retorno += Internacionalizacao.get("Report.Miss");
        } else {
            Retorno += Internacionalizacao.get("Report.Hit");
        }
        if (Subs >= 0) {
            Retorno += " - " + Internacionalizacao.get("Report.Substitution") + ". " + Internacionalizacao.get("Report.Block.Abb") + "." + Subs;
        }
        return Retorno;
    }

    public Object[] toArray() {
        int i = 0;
        Object[] Ret = new Object[7];

        Ret[i++] = new Integer(Numero);
        Ret[i++] = new Integer(Posicao);

        if (Natureza == 'G') {
            Ret[i++] = new String(Internacionalizacao.get("Report.Write"));
        } else {
            Ret[i++] = new String(Internacionalizacao.get("Report.Read"));
        }
        Ret[i++] = new Integer(Bloco);
        Ret[i++] = new Integer(Tempo);
        if (Resultado == 'E') {
            Ret[i++] = new String(Internacionalizacao.get("Report.Miss"));
        } else {
            Ret[i++] = new String(Internacionalizacao.get("Report.Hit"));
        }
        if (Subs < 0) {
            Ret[i++] = new String(" ");
        } else {
            Ret[i++] = new Integer(Subs);
        }
        return Ret;
    }

    public String toXml() {
        String Ret = "<acesso ";
        Ret += "numero=\"" + Numero + "\" ";
        Ret += "posicao=\"" + Posicao + "\" ";
        if (Natureza == 'G') {
            Ret += "natureza=\"gravacao\" ";
        } else {
            Ret += "natureza=\"leitura\" ";
        }
        Ret += "bloco=\"" + Bloco + "\" ";
        Ret += "tempo=\"" + Tempo + "\" ";

        if (Resultado == 'E') {
            Ret += "resultado=\"erro\" ";
        } else {
            Ret += "resultado=\"acerto\" ";
        }
        if (Subs >= 0) {
            Ret += "subs=\"" + Subs + "\" ";
        }
        Ret += " />";
        return Ret;
    }
};