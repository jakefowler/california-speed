#!/bin/bash

for f in *.svg
do
    sed -i 's/#000000/#5c5c4b/g' $f
    sed -i 's/#ffffff/#f7f7f6/g' $f
    sed -i 's/#ff5555/#ff6961/g' $f
    sed -i 's/#aa0000/#d03c3c/g' $f
    # sed -i 's/green/#77dd77/g' $f
    # sed -i 's/blue/#aec6cf/g' $f
    # sed -i 's/#FC4/#fdec96/g' $f
    # sed -i 's/#44F/#3e6ca2/g' $f
    # sed -i 's/<svg/<svg fill="#5c5c4b" /' $f
done