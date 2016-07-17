# Play-Java-Twitter

create twitter by using play framework 2.x in java

## How to build 
use lightbend activator


## How to run 

1.  checkout: https://github.com/SoichiSumi/play-java-twitter/commit/38ba4560cce81463aa7cee3d5fc0209480d0a308
(latest revision requires mysql on localhost:3306)

2.  do "activator run" at project root

3.  access localhost:9000 by browser

## Tasks that I want to do
Scaling
* replication of database by using mysql DONE
* partisioning of tweet table DONE
* ~~use memcached to cache query results~~ 
  * →use ehcache because memcached api is not exist currently DONE

## Screen Shot
![ScreenShot](https://github.com/SoichiSumi/play-java-twitter/blob/master/ScreenShot.png?raw=true "Screen Shot")

## References
http://www.atmarkit.co.jp/news/201004/19/twitter.html
http://www.slideshare.net/nkallen/q-con-3770885
