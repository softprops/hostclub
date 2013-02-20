# hostclub

a party club for your host mappings (under constuction)

## install

_make a backup of /etc/hosts_.

install with conscript ( you will need to build and publish the project locally )

    $ cs softprops/hostclub

## usage


     $ hc <command> <options>
     
      Commands:
      
      map, host, ip, ls, help, completion
      
### map 

Maps a hostname to an ip
   
     $ hc map foo.com 127.0.0.1
   
### host
    
Shows ip mapped to host

     $ hc host foo.com
      
### ip

Lists hosts mapped to ip

     $ hc ip 127.0.0.1
      
### ls

Lists managed host mappings

     $ hc ls
      
### help

Shows help info

     $ hc help

### completion

Experimental bash completion integration

     $ hc completion >> ~/.profile

This will enable tab completion of most commands

Doug Tangren (softprops) 2013
