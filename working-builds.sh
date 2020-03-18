#!/bin/bash

# cd /Users/vpalepu/phd-open-source/tacoco-data/projects

ls -1 *zip | wc -l

for x in /Users/vpalepu/phd-open-source/tacoco-data/projects/*zip; do
    project_dir=$(echo $x | cut -d "." -f 1)
    echo $x
    echo $project_dir
    unzip $x -d /Users/vpalepu/phd-open-source/tacoco-data/projects
    mvn exec:java -Pfind-working-build -Dexec.args="$project_dir" -q
    rm -rf $project_dir
    break
done