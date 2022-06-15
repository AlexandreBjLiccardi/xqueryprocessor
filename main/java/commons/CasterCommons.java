package commons;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Commons methods, for object manipulation (specially formats and types transforms)
 * @version		1a
 * @author		alexandre.liccardi
 * @date		05/03/2018
 */
public class CasterCommons {
	
	private static final String[] numericed_replacer = {","};
	private static final String[] numericed_deleter = {};
	private static final String numericed_regexp_deleter = "[^(\\d\\\\.)]";
	private static final String[] indiced_equivalent = {"long","integer","int"};
	private static final String[] indiced_rounded = {"float","double"};
	private static final String[] floated_equivalent = {"float","double","long","integer","int"};
	private static final String[] dated_regexp_formats_date = {	"dd/MM/yyyy","dd MM yyyy","dd-MM-yyyy","dd.MM.yyyy","dd_MM_yyyy","ddMMyyyy",
																"MM/dd/yyyy","MM dd yyyy","MM-dd-yyyy","MM.dd.yyyy","MM_dd_yyyy","MMddyyyy",
																"yyyy/MM/dd","yyyy MM dd","yyyy-MM-dd","yyyy.MM.dd","yyyy_MM_dd","yyyyMMdd"
															};
	private static final String[] dated_regexp_formats_hour= {"HH:mm:ss","HH-mm-ss","HH_mm_ss","HHmmss"};
	private static final String[] dated_regexp_formats_sep= {" ","-","_",":"," "};
	
	private static final String[] booled_regexp_formats_t = {"true","vrai","t","v","1"};
	private static final String[] booled_regexp_formats_f = {"false","faux","f","0"};
	
	/**
	 * Generic cast
	 *  First tries without string interpretation, then tries to explain the content.
	 * @param theOneToCast	Variable containing "the value"
	 * @param classToCast	Variable used to define the return custom class
	 * @return	Value casted in the custom class
	 * @throws ClassNotFoundException
	 */
    @SuppressWarnings("unchecked")
	public static <G,T> T cast_2(G theOneToCast, Class<T> classToCast) throws ClassNotFoundException {
   		if(classToCast.isAssignableFrom(theOneToCast.getClass()))return (T) theOneToCast ;
   		String destClass =classToCast.getSimpleName().toLowerCase();
    	return (T) cast_2t(destClass, theOneToCast) ;
    }
    
	/**
	 * Generic cast
	 *  First tries without string interpretation, then tries to explain the content.
	 * @param theOneToCast	Variable containing "the value"
	 * @param WildTypeName	Name of the custom class, simplified WILD type (@type)
	 * @return	Value casted in the custom class
	 * @throws ClassNotFoundException
	 */    
    @SuppressWarnings("unchecked")
	public static <G,T> T cast_2(G theOneToCast, String WildTypeName) throws ClassNotFoundException {
    	return (T) cast_2(theOneToCast, cast_getTypeAsClass(WildTypeName)) ;
    }
    
    /**
     * Attributes a JAVA class, from a simplifed WILD type
     * @param WildTypeName	Simplified WILD type (@type)
     * @return	JAVA Class
     * @throws ClassNotFoundException
     */
    public static Class cast_getTypeAsClass(String WildTypeName) throws ClassNotFoundException{
    	WildTypeName = WildTypeName.toLowerCase();
    	HashMap<Class, List<String>> mTypes = new HashMap() ;
    	mTypes.put(String.class, Arrays.asList(new String[]{"string"}));
    	mTypes.put(Float.class, Arrays.asList(new String[]{"float","double","long","numeric"}));
    	mTypes.put(Integer.class, Arrays.asList(new String[]{"long","integer"}));
    	mTypes.put(Date.class, Arrays.asList(new String[]{"date","time","timestamp"}));
    	for(Entry<Class,List<String>> e:mTypes.entrySet())if(e.getValue().contains(WildTypeName)) return e.getKey();
		return null;
    }
    
   /**
	* Generic cast
	*  First tries without string interpretation, then tries to explain the content.
    * @param caster	Name of the custom return class
    * @param value	Variable containing "the value"
    * @return	Value casted in the custom class
    * @throws ClassNotFoundException
    */
    @SuppressWarnings("unchecked")
	public static <T>  T cast_2t(String caster, T value) throws ClassNotFoundException {
    	Object[] toRet_prox = cast_2t_proxed(caster, value) ;
    	if((Boolean)toRet_prox[0]) return (T)(toRet_prox[1]);
    	Object[] toRet_stringed = cast_2t_stringed(caster, value) ;
    	if((Boolean)toRet_stringed[0]) return (T)toRet_stringed[1];
    	return null ;
    } 
    
    /**
 	* Generic cast without string interpretation
    * @param caster	Name of the custom return class
    * @param value	Variable containing "the value"
    * @return	Value casted in the custom class
    * @throws ClassNotFoundException
    */
    public static <G> Object[] cast_2t_proxed(String caster, G value) throws ClassNotFoundException{
    	if (value ==null) return new Object[]{true,null};
        if (caster==null||caster.equals("string")) return new Object[]{true, String.valueOf(value).trim()};
        else caster = caster.trim().toLowerCase();
        String searchClass = value.getClass().getSimpleName().toLowerCase();
        Object toRet = null ;
        Boolean toRetSuccess = false ;
        try {
	        if (caster.equals("long") && Arrays.asList(indiced_equivalent).contains(searchClass)){
	        	toRet =(long)value;
	        }
	        else if (caster.equals("long") && Arrays.asList(indiced_rounded).contains(searchClass)){
	        	toRet = Math.round((float) value);
	        }
	        else if ((caster.equals("integer") || caster.equals("int")) && Arrays.asList(indiced_equivalent).contains(searchClass)){
	        	toRet =(int)value;
	        }
	        else if ((caster.equals("integer") || caster.equals("int")) && Arrays.asList(indiced_rounded).contains(searchClass)){
	        	if(value instanceof Double)toRet = Math.round((double)value);
	        	else toRet = Math.round((float)value);
	        }    
	        else if (caster.equals("float")&& Arrays.asList(floated_equivalent).contains(searchClass)){
	        	if(value instanceof Double)toRet = ((Double)value).floatValue();
	        	else if(value instanceof Long)toRet = ((Long)value).floatValue();
	        	else toRet = ((Integer)value).floatValue();
	    	}
	        else if (caster.equals("double")&& Arrays.asList(floated_equivalent).contains(searchClass)){
	        	if(value instanceof Float)toRet = ((Float)value).doubleValue();
	        	else if(value instanceof Long)toRet = ((Long)value).doubleValue();
	        	else toRet = ((Integer)value).doubleValue();
	    	}
	    	else if (caster.equals("boolean")){
	    		Integer toRet2 = (Integer)cast_2t_proxed("int",value)[1];
	    		if(toRet2!=null&&toRet2>0)toRet = (boolean) true;
	    		else if(toRet2!=null&&toRet2==0)toRet = (boolean) false;
	    	}
	        else return new Object[]{toRetSuccess,null};
	        return new Object[]{toRetSuccess,toRet};
	   
        }catch (Exception e){
	    	e.printStackTrace();
	        return new Object[]{false,null};
	    }
    }
    
    /**
 	* Generic cast with string interpretation (tries to explain the content)
    * @param caster	Name of the custom return class
    * @param value	Variable containing "the value"
    * @return	Value casted in the custom class
    * @throws ClassNotFoundException
    */
    
    public static <G> Object[] cast_2t_stringed(String caster, G value) throws ClassNotFoundException{
		if (value ==null) return new Object[]{true,null};
        if (caster==null||caster.equals("string")) return new Object[]{true, String.valueOf(value).trim()};
        else caster = caster.trim().toLowerCase();
        final String str_value = String.valueOf(value).trim();
        String str_value_low = str_value.toLowerCase();
        Object toRet = null ;
        try{switch(caster) {
        	case("long"):
	            	for (String rep:numericed_replacer) str_value_low = str_value_low.replaceAll(rep, ".");
	            	for (String rep:numericed_deleter) str_value_low = str_value_low.replaceAll(rep, "");
	            	if(str_value_low.contains("."))toRet = Math.round(Float.parseFloat(str_value_low.replaceAll(numericed_regexp_deleter,"")));
	            	else toRet = Long.parseLong(str_value_low.replaceAll(numericed_regexp_deleter,""));
            	break;
        	case("integer"): case("int"):
	            	for (String rep:numericed_replacer) str_value_low = str_value_low.replaceAll(rep, ".");
	            	for (String rep:numericed_deleter) str_value_low = str_value_low.replaceAll(rep, "");
	            	if(str_value_low.contains("."))toRet = Math.round(Float.parseFloat(str_value_low.replaceAll(numericed_regexp_deleter,"")));
	            	else toRet = Integer.parseInt(str_value_low.replaceAll(numericed_regexp_deleter,""));
            	break;
        	case("float"):
	            	for (String rep:numericed_replacer) str_value_low = str_value_low.replaceAll(rep, ".");
	            	for (String rep:numericed_deleter) str_value_low = str_value_low.replaceAll(rep, "");
	            	toRet = Float.parseFloat(str_value_low.replaceAll(numericed_regexp_deleter,""));
	        	break;
        	case("double"):
	            	for (String rep:numericed_replacer) str_value_low = str_value_low.replaceAll(rep, ".");
	            	for (String rep:numericed_deleter) str_value_low = str_value_low.replaceAll(rep, "");
	            	toRet = Double.parseDouble(str_value_low.replaceAll(numericed_regexp_deleter,""));
	        	break;
        	case("boolean"):
	        		if(Arrays.asList(booled_regexp_formats_t).contains(str_value_low))toRet=true;
	        		else if(Arrays.asList(booled_regexp_formats_f).contains(str_value_low))toRet=false;
	        	break;
        	case("date"):
        		for(String formatdate:dated_regexp_formats_date)for(String formathour:dated_regexp_formats_hour)for(String formatsep:dated_regexp_formats_sep){
            		SimpleDateFormat formatter = new SimpleDateFormat(formatdate+formatsep+formathour);
            		try{
            			Date dt = formatter.parse(str_value_low) ;
            			return new Object[]{true,dt};
            		}catch(Exception e){continue;}     			
            	}
        		break;
            default : return new Object[]{true,value}; 
        }   return new Object[]{true,toRet};
        }	catch (Exception e){
        	e.printStackTrace();
            return new Object[]{false,str_value};
        }
    }
    
    /**
     * Encode a common JAVA string to a XML coded string. 
     * @param toEscape    Chaîne à transformer
     * @return
     */
    public static String cast_xmlEscape(Object toEscape){
        return toEscape.toString()
                .replaceAll("&","&amp;")
                .replaceAll("\"","&quot;")
                .replaceAll("'","&apos;")
                .replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
    }

    /**
     * Decode a XML coded string, to a common JAVA string
     * @param toEscape    String to transforms
     * @return
     */
    public static String cast_xmlUnescape(Object toEscape){
        return toEscape.toString()
                .replaceAll("&amp;","&")
                .replaceAll("&quot;","\"")
                .replaceAll("&apos;","'")
                .replaceAll("&lt;","<")
                .replaceAll("&gt;",">");
    }
    
    /**
     * Return a high level definition of the class, specially simplifying arrays to Object[].
     * GetClass customization.
     * @param tryO JAVA valued object
     * @return Class name
     */
    public static String cast_getClass(Object tryO){
    	if(tryO.getClass().isArray()) return "Object[]";
    	if(tryO instanceof List) return "List<Object>";
    	if(tryO instanceof Hashtable) return "Hashtable<Object,Object>";
    	if(tryO instanceof Map) return "HashMap<Object,Object>";
    	return tryO.getClass().getSimpleName();
    }
    	
    /**
     * Return a high level definition of the class, at asimplified level.
     * GetClass customization.
     * @param tryO JAVA valued object
     * @return Class name
     */
    public static String cast_getSimpleClass(Object tryO){
    	if(tryO.getClass().isAssignableFrom(Double.class)) return "Double" ; 
    	String wClass = cast_getClass(tryO) ;
    	switch(wClass){
	    	case "Date" : return "Date" ;
	    	case "String" : return "String" ;
	    	case "Integer":case "Float":case "Double": return "Double" ;
	    	case "Boolean" : return "Boolean" ;
	    	case "HashMap<Object,Object>" : return "HashMap" ;
	    	case "Hashtable<Object,Object>" : return "HashMap" ;
	    	case "List<Object>" : return "List" ;
	    	default : return wClass ;
    	}
    }	

    /**
     * "Human readable" conversion
     * @param i_returnUpdater    Element to convert
     * @return    Readable string
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String cast_2String(Object i_returnUpdater){
        String toRet ;
        switch(i_returnUpdater.getClass().getSimpleName()){
        case "DTMNodeList":
            toRet = "<List>\n";
            NodeList returnUpdaterNL = (NodeList) i_returnUpdater;
            for(int i =0;i<returnUpdaterNL.getLength();i++)toRet+="\n<Elt6d>\n"+cast_xmlEscape(cast_2String(returnUpdaterNL.item(i)))+"\n</Elt6d>";
             return toRet+"\n</List>";
        case "Node":case "DeferredElementImpl":
            StringWriter sw = new StringWriter();
             try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                t.transform(new DOMSource((Node) i_returnUpdater), new StreamResult(sw));
             } catch (TransformerException te) {
                te.printStackTrace();
             }
             return sw.toString();
        case "Object[]":
            toRet = "<array>";
            Object[] returnUpdater = (Object[]) i_returnUpdater;
            for(Integer i =0; i < returnUpdater.length; i++)toRet += "<Elt6d name='"+i+"'>"+cast_xmlEscape(cast_2String(returnUpdater[i]))+"</Elt6d>";
            toRet +=     "</array>";
            return toRet;
        case "List<Object>":
            toRet = "<list>";
            List<Object> returnUpdaterlist = (List<Object>) i_returnUpdater;
            for(Integer i =0; i < returnUpdaterlist.size(); i++)if(i<2500)toRet +=  "<Elt6d name='"+i+"'>"+cast_xmlEscape(cast_2String(returnUpdaterlist.get(i)))+"</Elt6d>";
            toRet +=     "</list>";
            return toRet;
        case "Hashtable<Object,Object>":
            toRet = "<map>";
            Hashtable<Object,Object> returnUpdatertable = (Hashtable<Object,Object>) i_returnUpdater;
            Iterator it = returnUpdatertable.entrySet().iterator();
            int i = 0 ;
            while (it.hasNext()) {
                if(i++>2500)break;
                Map.Entry pairs = (Map.Entry)it.next();
                toRet +=  "<Elt6d name='"+String.valueOf(pairs.getKey()).replaceAll("'","\\'")+"'>"+cast_xmlEscape(cast_xmlEscape(String.valueOf(pairs.getValue())))+"</Elt6d>";
            }
            toRet +=     "</map>";
            return toRet;
        case "HashMap<Object,Object>":
            toRet = "<map>";
            HashMap<Object,Object> returnUpdatermap = (HashMap<Object,Object>) i_returnUpdater;
            Iterator itmap = returnUpdatermap.entrySet().iterator();
            int i2 = 0 ;
            while (itmap.hasNext()) {
                if(i2++>2500)break;
                Map.Entry pairs = (Map.Entry)itmap.next();
                toRet +=  "<Elt6d name='"+String.valueOf(pairs.getKey()).replaceAll("'","\\'")+"'>"+cast_xmlEscape(String.valueOf(pairs.getValue()))+"</Elt6d>";
            }
            toRet +=     "</map>";
            return toRet;
        default:
            return String.valueOf(i_returnUpdater);
        }
    }

/**
 * Caster for generic transforms
 * If a multi-valued object in input : gets a map
 * If a single-valued object in input : gets the value    
 * @param toCast	A valued Object
 * @return	A value or a Map, in which everything is indexed as a map
 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object cast_asMap(Object toCast){
    	if(toCast == null)return null;
    	if(toCast.getClass().isArray()){
    		Integer i=0 ;
    		Object[] toCast_array = (Object[]) toCast ;
    		HashMap<String, Object> toRet = null;
    		for(Object tc:toCast_array){
    			if(toRet == null) toRet = new HashMap();
    			toRet.put((i++).toString(), cast_asMap(tc));
    		}
    		return toRet ;
    	}
    	if(toCast instanceof Map){
    		HashMap<Object,Object> toCast_map = (HashMap) toCast ;
    		HashMap<String, Object> toRet = null ;
    		for(Entry tc:toCast_map.entrySet()){
    			if(toRet == null) toRet = new HashMap();
    			toRet.put(tc.getKey().toString(), cast_asMap(tc.getValue()));
    		}
    		return (HashMap)toRet ; 
    	}
    	else return toCast ;
    }

	
}


