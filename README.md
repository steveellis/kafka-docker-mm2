# kafka-docker-mm2
Fork of docker setup to run MM.

I originally wanted to try to test this locally using the docker setup. That partially worked. What didn't work is offset translation between source and target where target consumers could begin consuming from where source consumers left off. I ended up getting offset translation to work on MSK connect however and decided to put the config that worked for me in the connector.properties file. Presumably this configuration could be made to work using Kafka connect as well but I didn't have time to try that out locally using docker.

Read the original [Medium article](https://medium.com/larus-team/how-to-setup-mirrormaker-2-0-on-apache-kafka-multi-cluster-environment-87712d7997a4) 
