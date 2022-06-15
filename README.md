# xqueryprocessor
Un outil simple, pour mobiliser avec JAVA, SAX et les commons le processus de traitement des XML par les xQuery.


alexandre.liccardi@ofb.gouv.fr, 04/11/2021

Version JAVA : 1.8

Librairies autoportées (voir pom.xml)

Appels décrits pour Windows (7+)

## Exemple 1 : Appel « standard »
`java -jar reporting2022_v2.jar "C:\xquery\GWB_2022.xquery" 
"C:\xquery\GWB_FRL_20210618.xml" "C:\xquery\output.html"
Appel : java –jar reporting2022_v2.jar`

- Argument 1 : "C:\xquery\GWB_2022.xquery" : Fichier contenant le 
script à exécuter, format xQuery
- Argument 2 : "C:\xquery\GWB_FRL_20210618.xml": Fichier contenant les 
données à confronter au script, correspond à la valeur de la variable 
source_url (variable externe xQuery)
- Argument 3 : "C:\xquery\output.html" : Fichier généré en sortie 
d’exécution du script
NB1. La numérotation compte « argument 1 » il s’agit en réalité du 
troisième depuis l’appel de java… Mais bien du premier vis-à-vis
reporting2022_v2.jar appelé.
## Exemple 2 : utiliser une variable supplémentaire
`java -jar reporting2022_v2.jar "C:\xquery\GWB_2022.xquery" 
"C:\xquery\GWB_FRL_20210618.xml" "C:\xquery\output.html"
"source_url_2=C:\xquery\GWB_FRJ_20210620.xml"`

Après l’argument 3, l’utilisateur peut déclarer des variables en 
plus. Il indiquera : NomdeVariable=ValeurDeVariable sans espace de 
séparation et séparé d’un signe « = ». Ici, on déclare une variable
(variable externe xQuery) en plus, source_url_2, de valeur 
« C:\xquery\GWB_FRJ_20210620.xml ».
### NB2. 
La variable DOIT apparaître dans le xQuery (ici en erreur).
### NB3. 
Les chemins de fichiers étant passés en variables, c’est ainsi 
que l’on va faire du cross schéma (tests sur plusieurs XML par un 
même xQuery).
## Exemple 3 : Utiliser des variables supplémentaires
`java -jar reporting2022_v2.jar "C:\xquery\GWB_2022.xquery" 
"C:\xquery\GWB_FRL_20210618.xml" "C:\xquery\output.html"
"source_url_2=C:\xquery\GWB_FRJ_20210620.xml"
"source_url_3=C:\xquery\GWB_FRJ_20210621.xml"
"source_url_4=C:\xquery\GWB_FRJ_20210622.xml"`

Toutes les variables passées après l’argument 3, sont prises en 
compte. Ici, on passe au script xQuery la liste de variables 
(variables externes xQuery) nommées :
- source_url_2, valuée à « C:\xquery\GWB_FRJ_20210620.xml »
- source_url_3, valuée à « C:\xquery\GWB_FRJ_20210621.xml »
- source_url_4, valuée à « C:\xquery\GWB_FRJ_20210622.xml »
### NB4. 
Les règles précédemment décrites s’appliquent sur toutes les 
variables. Elles DOIVENT être respectées.
### NB5. 
Les variables supplémentaires sont passées après les variables 
dites de base, relatives aux trois premiers arguments passés au jar. 
Donc, passer "source_url=C:\xquery\GWB_FRJ_20210620.xml" en fin 
d’appel écrasera l’information du deuxième argument (ici 
"C:\xquery\GWB_FRL_20210618.xml") ! L’ordre relatif au second 
argument DOIT être respecté en fonctionnement normal, mais PEUT être 
contourné pour utilisation spécifique.
### NB6. 
Rien n’empêchant de déclarer deux fois une même variable, on 
considère que c’est la dernière déclaration qui écrase la précédente. 
Ce fonctionnement, relevant du traitement des listes de JAVA n’est 
cependant pas garanti. L’unicité des variables déclarées DOIT donc 
être respectée.

## Fichiers et classes
- commons.CasterCommons.class : méthodes, pour la plupart statiques, utilisées pour des tâches classiques de transformation de formats de varaibles ("cast de types").
- launcher.Main.class : lancement des procédures (main)
- launcher.XqueryTest2.class : classe d'exécution des processus, relatifs aux xQuery.
