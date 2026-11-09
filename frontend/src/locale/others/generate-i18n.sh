#/bin/bash
# This script will create all i18n translate file for the differents langugages define in file "translates.json"
# Usage : ./generate-i18n.sh
# Important you need to install < jq > package (apt install jq)

locale_directory='src/locale'
translation_file='translates.json'

cd $locale_directory

# #Verify if translate file exist or not
# if [ ! -f "$translation_file" ]; then
# 	echo "File < $translation_file > doesnt exist in locale directory < $locale_directory >";
# 	exit;
# fi;

# #Get app default language
# default_language=$(jq -r '.default_language' $translation_file)
# #Get all app languages
# app_languages=$(jq -r '.languages[]' $translation_file)
# a=$(jq -rc '.translates[]' $translation_file)

a=$(jq -r '.translates' translates.json)
sample='[{"name":"foo"},{"name":"bar"}]'
echo "$a"
for t in $(echo "${sample}" |jq -r '.[]'); do
	echo ${t};
	echo "-";
done;

#Apply translation automatisation for each languages expect if langugages is app default languages





#Apply translation automatisation for each languages expect if langugages is app default languages
# for language in $app_languages; do
# 	#sed '/<source>/{p;s/source/target/g;}' messages.xlf > "messages-test-$language.xlf";
# 	if [ ! $language = $default_language ]; then
# 		echo "$translate_file" |jq -r ".translates[].targets.$language";
# 	fi
# done
#echo $app_languages
#cp messages.xlf messages-test.xlf

#sed '/<source>/{p;s/source/target/g;}' messages.xlf

# for translate in "$(echo "$translate_file" |jq -r \".translates[] | .source + ':' + .target\")"; do
# 	source=$(echo "$translate" |awk -F":" '{print $1}');
# 	target=$(echo "$translate" |awk -F":" '{print $2}');
# 	echo "$target";
# done;