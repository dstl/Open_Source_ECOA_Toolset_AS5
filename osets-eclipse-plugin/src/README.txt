Wireshark ELI script
====================

A script to use with wireshark that  decodes the ELI headers.

Place the ELI.lua file in your .wireshark/plugins directory prior to starting wireshark.

The script require to import a file called IDs.lua, which needs to be created from the IDs.xml file using the perl script IDs_to_lua.pl.
In order to generate it do 'cat IDs.xml | IDs_to_lua.pl > IDs.lua
Also note that the ELI.lua script uses an absolute path for the IDs.lua file, so this may need adjusting (particularly
if you are on Linux/Unix), as it is set to "D:/IDs.lua"


