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
   
     $ hc map foo.com 127.0.0.1
   
### host
    
     $ hc host foo.com
      
### ip

     $ hc map 127.0.0.1
      
### ls

     $ hc ls
      
### help

     $ hc help

### completion

Experimental bash completion integration

     $ hc completion >> ~/.profile

This will enable tab completion of most commands

Doug Tangren (softprops) 2013
