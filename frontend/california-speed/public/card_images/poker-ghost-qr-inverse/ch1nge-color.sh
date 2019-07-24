#!/bin/bash

for f in *.svg
do
    sed -i 's/#5c5c4b/white/g' $f
    sed -i 's/#f7f7f6/#5c5c4b/g' $f
    sed -i 's/white/#f7f7f6/g' $f
    # sed -i 's/red/#ff6961/g' $f
    # sed -i 's/#FC4/#fdec96/g' $f
    # sed -i 's/#44F/#3e6ca2/g' $f
    # sed -i 's/<svg /<svg fill="#5c5c4b" /' $f
done