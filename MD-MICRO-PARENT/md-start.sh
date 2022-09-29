#!/bin/sh
# Start all md-microservices
cd /root/md-microservices/config-server/
nohup java -Xms512m -Xmx512m -jar config-server-1.0.0.jar > config-server.log &
sleep 30
cd /root/md-microservices/md-seguridad/
nohup java -Xms512m -Xmx512m -jar md-seguridad-1.0.0.jar --spring.profiles.active=dev > md-seguridad.log &
cd /root/md-microservices/md-catalogo-rcor/
nohup java -Xms512m -Xmx512m -jar md-catalogo-rcor-1.0.0.jar --spring.profiles.active=dev > md-catalogo-rcor.log &
cd /root/md-microservices/md-jcard/
nohup java -Xms512m -Xmx512m -jar md-jcard-1.0.0.jar --spring.profiles.active=dev > md-jcard.log &
cd /root/md-microservices/md-global/
nohup java -Xms512m -Xmx512m -jar md-global-1.0.0.jar --spring.profiles.active=dev > md-global.log &
cd /root/md-microservices/md-sistar/
nohup java -Xms512m -Xmx512m -jar md-sistar-1.0.2.jar --spring.profiles.active=dev > md-sistar.log &
cd /root/md-microservices/md-contrato/
nohup java -Xms512m -Xmx512m -jar md-contrato-1.0.0.jar --spring.profiles.active=dev > md-contrato.log &
cd /root/md-microservices/md-persona/
nohup java -Xms512m -Xmx512m -jar md-persona-1.0.0.jar --spring.profiles.active=dev > md-persona.log &
cd /root/md-microservices/md-movimientos/
nohup java -Xms512m -Xmx512m -jar md-movimientos-1.0.0.jar --spring.profiles.active=dev > md-movimientos.log &
cd /root/md-microservices/md-basicopresencial/
nohup java -Xms512m -Xmx512m -jar md-basicopresencial-1.0.0.jar --spring.profiles.active=dev > md-basicopresencial.log &
cd /root/md-microservices/md-basicodigital/
nohup java -Xms512m -Xmx512m -jar md-basicodigital-1.0.0.jar --spring.profiles.active=dev > md-basicodigital.log &
cd /root/md-microservices/md-producto/
nohup java -Xms512m -Xmx512m -jar md-producto-1.0.0.jar --spring.profiles.active=dev > md-producto.log &
cd /root/md-microservices/md-apipersona-adapter/
nohup java -Xms512m -Xmx512m -jar md-apipersona-adapter-1.0.0.jar --spring.profiles.active=dev > md-apipersona-adapter.log &