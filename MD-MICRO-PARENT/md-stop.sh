#!/bin/sh
# Stop all md-microservices
pkill -f 'java.*config-server'
pkill -f 'java.*md-seguridad'
pkill -f 'java.*md-catalogo'
pkill -f 'java.*md-jcard'
pkill -f 'java.*md-global'
pkill -f 'java.*md-sistar'
pkill -f 'java.*md-contrato'
pkill -f 'java.*md-persona'
pkill -f 'java.*md-movimientos'
pkill -f 'java.*md-basicopresencial'
pkill -f 'java.*md-basicodigital'
pkill -f 'java.*md-producto'
pkill -f 'java.*md-apipersona-adapter'


