# hostclub

a party club for your host mappings

## install

_make a backup of /etc/hosts_.

install with conscript ( you will need to build and publish the project locally )

    $ cs softprops/hostclub

## usage

     $ hc <command> <options>
     
      Commands:

      map, unmap, clear, aliases, alias, host, ip, ls, help, swap, completion 

You may substitute an alias anywhere an ip is expected.

### map 

Maps a hostname to an ip
   
     $ hc map foo.com 127.0.0.1

### unmap

Unmaps a hostname

    $ hc unmap foo.com

### clear

Clears all managed host mappings

    $ hc clear

### aliases

Lists all current aliases

    $ hc aliases

### alias

Stores an alias for a given ip

    $ hc alias name ip
    $ hc alias name

### host
    
Shows ip mapped to host

     $ hc host foo.com
      
### ip

Lists hosts mapped to ip

     $ hc ip 127.0.0.1

### swap

Swaps two ips.

    $ hc swap 127.0.0.1 192.168.0.10
      
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
