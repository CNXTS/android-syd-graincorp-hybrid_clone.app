pwd
env

rvm list

bundle install
bundle exec fastlane update_plugins

TAG_NUM=${tag_name}
REF=${ref}

echo ${TAG_NUM}
echo ${REF}

if [ -z "$TAG_NUM" ] && [ "$REF" != "refs/heads/develop" ] && ! [[ $REF =~ ^(refs\/heads\/release.*)$ ]]
then
    echo "No tag detected or ref is neither develop nor release. Building normally"
	bundle exec fastlane build_only
else
	echo "Tag detected or ref is either develop or release. Creating builds for HockeyApp upload."
    bundle exec fastlane build_and_upload
fi
