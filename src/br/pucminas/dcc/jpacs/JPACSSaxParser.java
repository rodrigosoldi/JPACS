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

package br.pucminas.dcc.jpacs;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

/**
 * Parser para leitura de arquivos XML
 */
public class JPACSSaxParser extends DefaultHandler {
    
    private static final int Irreconhecido=0;
    private static final int Start=1;
    private static final int Memoria=2;
    private static final int MemCache=3;
    private static final int MemPrincipal=4;
    private static final int Lru=5;
    private static final int Fifo=6;
    private static final int Sequencial=7;
    private static final int Paralelo=8;
    private static final int CopyBack=9;
    private static final int WriteThrought=10;
    
    public String RamTamanho;
    public String RamTempoAcesso;
    public String CacheTamanho;
    public String CacheTempoAcesso;
    public String CacheTamanhoBloco;
    public boolean CacheLru;
    public boolean CacheSequencial;
    public boolean CacheCopyBack;
    
    public static final String DtdFileName="JPACS.dtd";
    
    public JPACSSaxParser(){
        super();
        
        RamTamanho="1000";
        RamTempoAcesso="100";
        CacheTamanho="10";
        CacheTempoAcesso="10";
        CacheTamanhoBloco="10";
        CacheLru=true;
        CacheSequencial=true;
        CacheCopyBack=true;
    }
    
    @Override
    public String toString(){
        String OutStr="\n\t<config>"
                +"\n\t\t<memoria>"
                +"\n\t\t\t<principal tamanho=\""+RamTamanho+"\" tempoacesso=\""+RamTempoAcesso+"\" />"
                +"\n\t\t\t<cache tamanho=\""+CacheTamanho+"\" tempoacesso=\""+CacheTempoAcesso+"\" tamanhobloco=\""+CacheTamanhoBloco+"\">"
                +"\n\t\t\t\t<";
        
        if(CacheLru)
            OutStr+="lru /> <";
        else
            OutStr+="fifo /> <";
        
        if(CacheSequencial)
            OutStr+="sequencial /> <";
        else
            OutStr+="paralelo /> <";
        
        if(CacheCopyBack)
            OutStr+="copyback />";
        else
            OutStr+="writethrought />";
        
        OutStr+="\n\t\t\t</cache>\n\t\t</memoria>\n\t</config>\n";
        
        return OutStr;
    }
    
    public void salvarParaArquivo(String FileName){
        File Archive=new File(FileName);
        
        String OutStr="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                +"<!DOCTYPE JPACS SYSTEM \""+DtdFileName+"\">"
                +"\n"
                +"\n<JPACS>"
                +"\n   <config nome=\""+FileName+"\">"
                +"\n	   <memoria>"
                +"\n		   <principal tamanho=\""+RamTamanho+"\" tempoacesso=\""+RamTempoAcesso+"\" />"
                +"\n		   <cache tamanho=\""+CacheTamanho+"\" tempoacesso=\""+CacheTempoAcesso+"\" tamanhobloco=\""+CacheTamanhoBloco+"\">"
                +"\n                <";
        
        if(CacheLru)
            OutStr+="lru /> <";
        else
            OutStr+="fifo /> <";
        
        if(CacheSequencial)
            OutStr+="sequencial /> <";
        else
            OutStr+="paralelo /> <";
        
        if(CacheCopyBack)
            OutStr+="copyback />";
        else
            OutStr+="writethrought />";
        
        OutStr+="\n		    </cache>\n    	 </memoria>\n   </config>\n</JPACS>";
        
        try{
            FileWriter Output=new FileWriter(Archive);
            Output.write(OutStr);
            Output.close();
            
        }catch(Exception e){
            System.out.println(e);
        }
        salvarArquivoDtd(Archive.getParent());
        
    }
    
    public void salvarArquivoDtd(String Path) {
        File Archive=new File(Path,DtdFileName);
        if(!Archive.exists()) {
            
            String OutStr="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    +"\n<!ELEMENT JPACS (config,relatorio*)>"
                    +"\n"
                    +"\n<!ELEMENT config (memoria)>"
                    +"\n<!ATTLIST config"
                    +"\n        nome CDATA #IMPLIED>"
                    +"\n"
                    +"\n<!ELEMENT memoria (principal,cache)>"
                    +"\n"
                    +"\n<!ELEMENT principal EMPTY>"
                    +"\n<!ATTLIST principal"
                    +"\n        tamanho CDATA #REQUIRED"
                    +"\n	     tempoacesso CDATA #REQUIRED>"
                    +"\n"
                    +"\n<!ELEMENT cache ((lru|fifo),(sequencial|paralelo),(copyback|writethrought))>"
                    +"\n<!ATTLIST cache"
                    +"\n        tamanho CDATA #REQUIRED"
                    +"\n  	     tempoacesso CDATA #REQUIRED"
                    +"\n	     tamanhobloco CDATA #REQUIRED>"
                    +"\n"
                    +"\n<!ELEMENT lru EMPTY>"
                    +"\n<!ELEMENT fifo EMPTY>"
                    +"\n<!ELEMENT sequencial EMPTY>"
                    +"\n<!ELEMENT paralelo EMPTY>"
                    +"\n<!ELEMENT copyback EMPTY>"
                    +"\n<!ELEMENT writethrought EMPTY>"
                    +"\n"
                    +"\n<!ELEMENT relatorio (acesso*,estatisticas)>"
                    +"\n<!ATTLIST relatorio"
                    +"\n	nome CDATA #IMPLIED>"
                    +"\n"
                    +"\n<!ELEMENT acesso EMPTY>"
                    +"\n<!ATTLIST acesso "
                    +"\n	numero CDATA #REQUIRED"
                    +"\n	posicao CDATA #REQUIRED"
                    +"\n	natureza (leitura|gravacao) #REQUIRED"
                    +"\n	bloco CDATA #REQUIRED"
                    +"\n	tempo CDATA #REQUIRED"
                    +"\n	resultado (erro|acerto) #REQUIRED"
                    +"\n	subs CDATA #IMPLIED>"
                    +"\n"
                    +"\n<!ELEMENT estastisticas (principal,cache,total)>"
                    +"\n"
                    +"\n<!ELEMENT memprincipal EMPTY>"
                    +"\n<!ATTLIST memprincipal"
                    +"\n	acessos CDATA #REQUIRED"
                    +"\n	tempoacesso CDATA #REQUIRED"
                    +"\n	tempototal CDATA #REQUIRED>"
                    +"\n"
                    +"\n<!ELEMENT memcache EMPTY>"
                    +"\n<!ATTLIST memcache"
                    +"\n	acessos CDATA #REQUIRED"
                    +"\n	tempoacesso CDATA #REQUIRED"
                    +"\n	tempototal CDATA #REQUIRED"
                    +"\n	taxaacerto CDATA #REQUIRED"
                    +"\n	taxaerro CDATA #REQUIRED>"
                    +"\n"
                    +"\n<!ELEMENT total EMPTY>"
                    +"\n<!ATTLIST total"
                    +"\n	comcache CDATA #REQUIRED"
                    +"\n	semcache CDATA #REQUIRED"
                    +"\n	ganho CDATA #REQUIRED>";
            
            try{
                FileWriter Output=new FileWriter(Archive);
                Output.write(OutStr);
                Output.close();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }
    
    public void parse(String FileName) throws Exception{
        File Archive=new File(FileName);
        SAXParserFactory factory=SAXParserFactory.newInstance();
        
        File DtdFile=new File(DtdFileName);
        if(DtdFile.exists())
            factory.setValidating(true);
        
        SAXParser saxParser=factory.newSAXParser();
        saxParser.parse(Archive, this);
        
    }
    
    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        String Com;
        int Tag;
        int i;
        
        if(sName.length()!=0)
            Com=sName;
        else
            Com=qName;
        
        /* Verifica qual tag foi aberta */
        if(Com.equals("config") || Com.equals("JPACS"))
            Tag=Start;
        else if(Com.equals("memoria"))
            Tag=Memoria;
        else if(Com.equals("cache"))
            Tag=MemCache;
        else if(Com.equals("principal"))
            Tag=MemPrincipal;
        else if(Com.equals("lru"))
            Tag=Lru;
        else if(Com.equals("fifo"))
            Tag=Fifo;
        else if(Com.equals("sequencial"))
            Tag=Sequencial;
        else if(Com.equals("paralelo"))
            Tag=Paralelo;
        else if(Com.equals("copyback"))
            Tag=CopyBack;
        else if(Com.equals("writethrought"))
            Tag=WriteThrought;
        else
            Tag=Irreconhecido;
        
        /* Processa a Tag */
        try{
            switch(Tag){
                case Start: //tag inicial de config
                    break;
                    
                case Memoria:
                    break;
                    
                case MemPrincipal:
                    for(i=0; i<attrs.getLength(); i++){
                        Com=attrs.getLocalName(i);
                        if(Com.length()==0)
                            Com=attrs.getQName(i);
                        
                        if(Com.equals("tamanho"))
                            RamTamanho=attrs.getValue(i);
                        else if(Com.equals("tempoacesso"))
                            RamTempoAcesso=attrs.getValue(i);
                    }
                    break;
                    
                case MemCache:
                    
                    for(i=0; i<attrs.getLength(); i++){
                        Com=attrs.getLocalName(i);
                        if(Com.length()==0)
                            Com=attrs.getQName(i);
                        
                        if(Com.equals("tamanho"))
                            CacheTamanho=attrs.getValue(i);
                        if(Com.equals("tempoacesso"))
                            CacheTempoAcesso=attrs.getValue(i);
                        if(Com.equals("tamanhobloco"))
                            CacheTamanhoBloco=attrs.getValue(i);
                    }
                    break;
                    
                case Lru:
                    CacheLru=true;
                    break;
                    
                case Fifo:
                    CacheLru=false;
                    break;
                    
                case Sequencial:
                    CacheSequencial=true;
                    break;
                    
                case Paralelo:
                    CacheSequencial=false;
                    break;
                    
                case CopyBack:
                    CacheCopyBack=true;
                    break;
                    
                case WriteThrought:
                    CacheCopyBack=false;
                    break;
                    
                default:
                    System.out.println("Error: undefined element '"+Com+"'");
            }
        }catch(Exception e){
            System.out.println("Error: "+e);
        }
    }
    
    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        //usado quando se fecha uma tag. Não necessário aqui.
        super.endElement(namespaceURI, sName, qName);
    }
    
    @Override
    public void error(SAXParseException e) throws SAXParseException {
        throw e;
    }
};