package launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import commons.CasterCommons;

/**
 * Classe pour tests xQuery
 * Le fonctionnement général est basé sur l'appel de la méthode process_i, à partir de 3 paramètres et d'un tableau d'arguments.
 * Les dépendances sont communiquées dans le fichier pom.
 * @author alexandre.liccardi
 *
 */
public class XqueryTest2 {
/**
 * Méthode de conversion des chemins de fichier, permet ne pas reprogrammer la capture du cas null
 * @param value	Chemin de fichier à convertir
 * @return Chemin de fichier converti ou null si null en entrée
 */
	private String valueNotNullAsFilePath(String value){
		if(value == null) return null;
		return "file:"+ value.replace("\\", "/") ;
	}

/**
 * Méthode de conversion du tableau de paramètres vers une liste compréhensible par la classe
 * Voir la doc détaillée. En résumé :
 * Les trois premiers paramètres sont exclus, pour les suivants ont découpe par "=" avec en 1 la clé et en 2 la valeur attribuée (qui est donc tronquée si un second "=" est proposé.
 * @param iValues	Tableau de paramètres en entrée
 * @return 			Liste de paramètres
 */

	private HashMap<String, String> cmdArrayToMap(String[] iValues){
		int i = 0;
		HashMap<String, String> toReturn =new HashMap<String, String>();
				if(iValues!=null)for(String iValue:iValues)	if(i++>2 && iValue!=null & iValue.contains("="))
					toReturn.put(iValue.split("=", 2)[0].trim(),iValue.split("=", 2)[1]);
		return toReturn;
	}
	
/**
 * Surcharge de la méthode process_i
 * @param XQFile	Chemin relatif ou absolu, sur disque, du script xQuery
 * @param XMLFile	Chemin relatif ou absolu, sur disque, du fichier XML à tester
 * @param OUTFile	Chemin relatif ou absolu, sur disque, du fichier dans lequel le programme écrit les résultats de l'excécution du script xQuery sur le fichier XML
 */
	public void process_i(String XQFile, String XMLFile, String OUTFile){
		process_i(XQFile, XMLFile, OUTFile, null);
	}

/**
 * Méthode de process d'un fichier xQuery, réalisé sur un fichier (référence de variable "source_url" dans le script) et acceptant un fichier local en sortie.
 * Des paramètres supplémentaires ou en surcharge peuvent être passés par un tableau spécifique (iValues).
 * @param XQFile	Chemin relatif ou absolu, sur disque, du script xQuery
 * @param XMLFile	Chemin relatif ou absolu, sur disque, du fichier XML à tester
 * @param OUTFile	Chemin relatif ou absolu, sur disque, du fichier dans lequel le programme écrit les résultats de l'excécution du script xQuery sur le fichier XML
 * @param iValues	Paramètres, avec les trois premiers termes non traités et les suivants du types clé=valeur. La "clé" doit être retrouvée en variable externe dans le script xQuery.
 */
	public void process_i(String XQFile, String XMLFile, String OUTFile, String[] iValues){
		InputStream inputStream = null ;
			FileOutputStream out_FileOutPut = null ;
			XQConnection conn = null;	
			try{
				HashMap<String, String> values = cmdArrayToMap(iValues);
				File out_File =  new File(OUTFile) ; 
				if(out_File.exists())out_File.delete();
				inputStream =  new FileInputStream(XQFile);//new ByteArrayInputStream(processer.getBytes());
				XQDataSource ds = new com.saxonica.xqj.SaxonXQDataSource();
				conn = ds.getConnection();
				XQPreparedExpression exp = conn.prepareExpression(inputStream);
				exp.bindString(new QName("source_url"), valueNotNullAsFilePath(XMLFile), conn.createAtomicType(XQItemType.XQBASETYPE_STRING));				
				for(HashMap.Entry<String, String> entryValues:values.entrySet())exp.bindString(new QName(entryValues.getKey()), valueNotNullAsFilePath(entryValues.getValue()), conn.createAtomicType(XQItemType.XQBASETYPE_STRING));
				XQResultSequence result = exp.executeQuery();	
				out_File.getParentFile().mkdirs();
				out_FileOutPut = new FileOutputStream(out_File);
				while (result.next()) out_FileOutPut.write(CasterCommons.cast_xmlUnescape(result.getItemAsString(null)).getBytes());
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{try{
				if(inputStream!=null)inputStream.close();
				if(out_FileOutPut!=null)out_FileOutPut.close();
				if(conn!=null)conn.close();
			}catch(Exception e){e.printStackTrace();}
			}
	}

	
}
