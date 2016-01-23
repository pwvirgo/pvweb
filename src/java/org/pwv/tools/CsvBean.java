package org.pwv.tools;

/**
 *
 * @author pwv
 */
public class CsvBean {

	private String infileName;
	private String htmlTable;
	private String msg;
	
	public String getInfileName() { return infileName; }
	public String getHtmlTable() { return htmlTable; }
	public String getMsg() { return msg; }
	
	public void setInfileName( String ifn) { infileName=ifn;}
	public void setHtmlTable( String htm) { htmlTable=htm; }
	public void setMsg (String msg) {this.msg=msg;}
	
}
