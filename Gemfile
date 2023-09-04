# Gemfile
source 'https://rubygems.org'

# Specifying minimum Ruby Version
# Version specifiers: http://ruby-doc.org/stdlib-2.0.0/libdoc/rubygems/rdoc/Gem/Version.html#class-Gem::Version-label-Preventing+Version+Catastrophe-3A
ruby '2.5.3'

gem 'fastlane', '2.190.0' # Choose your version of Fastlane. Can be left as is.

# Add anymore Ruby gems you need for the project with a specific version if required.
# Not specifying a version number gets the latest version of the gem on the first `bundle install`, but the version will be locked when the `Gemfile.lock` is generated.
plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
