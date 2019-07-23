#!/bin/bash

for f in *.svg
do
    sed -i 's/black/#5c5c4b/g' $f
    sed -i 's/white/#f7f7f6/g' $f
    sed -i 's/red/#ff6961/g' $f
    sed -i 's/green/#77dd77/g' $f
    sed -i 's/blue/#aec6cf/g' $f
    sed -i 's/#FC4/#fdec96/g' $f
    sed -i 's/#44F/#3e6ca2/g' $f
    sed -i 's/<svg /<svg fill="#5c5c4b" /' $f
done