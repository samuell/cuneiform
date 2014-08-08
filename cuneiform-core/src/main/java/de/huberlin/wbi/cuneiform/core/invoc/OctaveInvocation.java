package de.huberlin.wbi.cuneiform.core.invoc;

import java.util.List;

import de.huberlin.wbi.cuneiform.core.semanticmodel.CompoundExpr;
import de.huberlin.wbi.cuneiform.core.semanticmodel.JsonReportEntry;
import de.huberlin.wbi.cuneiform.core.semanticmodel.NotDerivableException;
import de.huberlin.wbi.cuneiform.core.semanticmodel.Ticket;

public class OctaveInvocation extends Invocation {

	public OctaveInvocation( Ticket ticket ) {
		super( ticket );
	}

	@Override
	protected String callFunction( String name, String... argValue ) {
		
		StringBuffer buf;
		boolean comma;
		
		buf = new StringBuffer();
		
		buf.append( name ).append( "( " );
		
		comma = false;
		for( String value : argValue ) {
			
			if( comma )
				buf.append( ", " );
			
			comma = true;
			
			buf.append( value );
		}
		
		buf.append( " )" );
		
		return buf.toString();
	}

	@Override
	protected String callProcedure( String name, String... argValue ) {
		return callFunction( name, argValue )+";\n";
	}

	@Override
	protected String clip( String varName ) {
		return varName+" = "+varName+"( 2:length( "+varName+" ) );\n";
	}

	@Override
	protected String comment( String comment ) {
		return "% "+comment+"\n";
	}

	@Override
	protected String copyArray( String from, String to ) {
		return to+" = "+from+";\n";
	}

	@Override
	protected String defFunctionLog() throws NotDerivableException {
		
		StringBuffer buf;
		
		buf = new StringBuffer();
		
		buf.append( "if( ~ischar( key ) ); error( 'Parameter ''key'' must be of type char.' ); end\n" )
			.append( "if( ~ischar( value ) ); error( 'Parameter ''value'' must be of type char.' ); end\n" )
			.append( "[fid msg] = fopen( '"+REPORT_FILENAME+"', 'a' );\n" )
			.append( "if( fid < 0 ); error( msg ); end\n" )
			.append( "fprintf( fid, '{" )
			.append( JsonReportEntry.ATT_TIMESTAMP+":"+System.currentTimeMillis()+"," )
			.append( JsonReportEntry.ATT_RUNID+":\""+getRunId()+"\"," )
			.append( JsonReportEntry.ATT_TASKID+":"+getTaskId()+"," );

		if( this.hasTaskName() )
			buf.append( JsonReportEntry.ATT_TASKNAME+":\""+getTaskName()+"\"," );
		
		buf.append( JsonReportEntry.ATT_LANG+":\""+getLangLabel()+"\"," )
			.append( JsonReportEntry.ATT_INVOCID+":"+getTicketId()+"," )
			.append( JsonReportEntry.ATT_KEY+":\"%s\"," )
			.append( JsonReportEntry.ATT_VALUE+":%s}\\n', key, value )\n" )
			.append( "if( fclose( fid ) < 0 ); error( 'Could not close report file' ); end" );		
		
		return defFunction( FUN_LOG, null, new String[] { "key", "value" }, buf.toString() );
	}

	@Override
	protected String defFunctionLogFile() throws NotDerivableException {
		
		StringBuffer buf;
		
		buf = new StringBuffer();
		
		buf.append( "if( ~ischar( key ) ); error( 'Parameter ''key'' must be of type char.' ); end\n" )
			.append( "if( ~ischar( value ) ); error( 'Parameter ''value'' must be of type char.' ); end\n" )
			.append( "[fid msg] = fopen( '"+REPORT_FILENAME+"', 'a' );\n" )
			.append( "if( fid < 0 ); error( msg ); end\n" )
			.append( "fprintf( fid, '{" )
			.append( JsonReportEntry.ATT_TIMESTAMP+":"+System.currentTimeMillis()+"," )
			.append( JsonReportEntry.ATT_RUNID+":\""+getRunId()+"\"," )
			.append( JsonReportEntry.ATT_TASKID+":"+getTaskId()+"," );

		if( this.hasTaskName() )
			buf.append( JsonReportEntry.ATT_TASKNAME+":\""+getTaskName()+"\"," );
		
		buf.append( JsonReportEntry.ATT_LANG+":\""+getLangLabel()+"\"," )
			.append( JsonReportEntry.ATT_INVOCID+":"+getTicketId()+"," )
			.append( JsonReportEntry.ATT_FILE+":\"%s\"," )
			.append( JsonReportEntry.ATT_KEY+":\"%s\"," )
			.append( JsonReportEntry.ATT_VALUE+":%s}\\n', file, key, value )\n" )
			.append( "if( fclose( fid ) < 0 ); error( 'Could not close report file' ); end" );		
		
		return defFunction( FUN_LOGFILE, null, new String[] { "file", "key", "value" }, buf.toString() );
	}

	@Override
	protected String defFunctionNormalize() throws NotDerivableException {
		
		return defFunction(
				FUN_NORMALIZE,
				null,
				new String[] { "channel", "f" },
				"sprintf( '"+getTicketId()
				+"_%d_%s', channel, f )\n" );
	}

	@Override
	protected String dereference( String varName ) {
		return varName;
	}

	@Override
	protected String fileSize( String filename ) {
		return "stat( "+filename+" ).size";
	}

	@Override
	protected String forEach( String listName, String elementName, String body ) {
		
		StringBuffer buf;
		
		buf = new StringBuffer();
		
		buf.append( "for i = 1:length( " ).append( listName ).append( ")\n" );
		buf.append( varDef( elementName, listName+"{ i };\n" ) );
		buf.append( body );
		buf.append( "\nend\n" );
		
		return buf.toString();
	}

	@Override
	protected String getShebang() {
		return "#!/usr/bin/octave -q\n";
	}

	@Override
	protected String getCheckPost() {
		return "";
	}

	@Override
	protected String getImport() {
		return "";
	}

	@Override
	protected String ifListIsNotEmpty( String listName, String body ) {
		return "if ~isempty( "+listName+" )\n"+body+"\nend\n";
	}

	@Override
	protected String ifNotFileExists( String fileName, String body ) {
		return "if exist( "+fileName+", 'file' ) == 2\n"+body+"\nend\n";
	}

	@Override
	protected String join( String... elementList ) {
		
		StringBuffer buf;
		boolean comma;
		
		buf = new StringBuffer();
		
		buf.append( "[" );
		
		comma = false;
		for( String element : elementList ) {
			
			if( comma )
				buf.append( " " );
			comma = true;
			
			buf.append( element );
		}
		buf.append( "]" );
		
		return buf.toString();
	}

	@Override
	protected String listAppend( String listName, String element ) {
		return listName+" = ["+listName+" {"+element+"}];";
	}

	@Override
	protected String listToBraceCommaSeparatedString(String listName,
			String stringName, String open, String close) {
		
		StringBuffer buf;
		
		buf = new StringBuffer();
		
		buf.append( stringName ).append( " = '" ).append( open );
		buf.append( "';\n__COMMA = false;\n" );
		buf.append( 
			octaveForLoop( "i", "length( "+listName+" )",
				"if( __COMMA ); ret = [ret ',']; end\n__COMMA = true;\n"
				+"ret = [ret "+listName+"{ i }];\n" ) );
		buf.append( "ret = [ret '" ).append( close ).append( "'];" );
		
		return buf.toString();
	}

	@Override
	protected String newList( String listName ) {
		return listName+" = {};\n";
	}

	@Override
	protected String quote( String content ) {
		return "'"+content+"'";
	}

	@Override
	protected String raise( String msg ) {
		return callProcedure( "error", quote( msg ) );
	}

	@Override
	protected String symlink(String src, String dest) {
		return "system( sprintf( 'ln -s %s %s', "+src+", "+dest+" ) )"; 
	}

	@Override
	protected String varDef( String varname, String value ) {
		return varname+" = "+value+";\n";
	}

	@Override
	protected String varDef( String varname, CompoundExpr ce )
			throws NotDerivableException {
		
		StringBuffer buf;
		int i;
		boolean comma;
		List<String> list;
		
		list = ce.normalize();
		
		buf = new StringBuffer();
		
		buf.append( varname )
			.append( " = {" );
		
		comma = false;
		for( i = 0; i < list.size(); i++ ) {
			
			if( comma )
				buf.append( ';' );
			
			comma = true;
			
			buf.append( " '" ).append( list.get( i ) ).append( "'" );
		}
		
		buf.append( "};\n" );
		
		return buf.toString();
	}
	
	private static String defFunction( String funName, String outputName, String[] inputNameList, String body ) {
		
		String ret;
		boolean comma;
		
		ret = "function ";
		
		if( outputName != null )
		 ret += outputName+" = ";
		
		ret += funName+"( ";
		
		comma = false;
		for( String inputName : inputNameList ) {
			
			if( comma )
				ret += ", ";
			
			comma = true;
			
			ret += inputName;
		}
		
		ret += " )\n"+body+"\nend\n";
		
		return ret;
	}

	private static String octaveForLoop( String runVar, String times, String body ) {
		return "for "+runVar+" = 1:"+times+"\n"+body+"\nend\n";
	}
}
