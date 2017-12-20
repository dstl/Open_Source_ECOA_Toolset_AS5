#!/usr/bin/perl -w

 @lines = <STDIN>;
 @output = "ID_list = {\n";
 foreach $line (@lines)
 {
    if ($line =~ m/<ID key=(.*) value=\"(.*)\"\/>/)
    {
    	# @output.push( "[$2] = $1,\n" );
    	push(@output, "[$2] = $1,\n" );
    }
 }
 # remove last line
 $output[$#output] =~ s/(.*),/$1/;
 push( @output, "}\n");
 
 print STDOUT @output ;
