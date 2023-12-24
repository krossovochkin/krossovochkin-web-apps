#!/bin/bash

set -e

GITHUB_TOKEN="${1}"
GITHUB_REPO="krossovochkin/personal-website"
GITHUB_BRANCH="master"

printf "\033[0;32mClean up website folder...\033[0m\n"
cd website
git checkout ${GITHUB_BRANCH}
git pull origin ${GITHUB_BRANCH}
shopt -s extglob

cd static/applications
rm -r *

cd ../../..
cp -a apps/dimensions-utils/build/dist/js/productionExecutable/. website/static/applications/dimensions-utils/
cp -a apps/time-utils/build/dist/js/productionExecutable/. website/static/applications/time-utils/
cp -a apps/color-utils/build/dist/js/productionExecutable/. website/static/applications/color-utils/
cp -a apps/card-soccer/build/dist/js/productionExecutable/. website/static/applications/card-soccer/

printf "\033[0;32mDeploying updates to GitHub...\033[0m\n"
cd website

MESSAGE="update apps $(date)"
git add .

if [[ -z "${GITHUB_TOKEN}" || -z "${GITHUB_REPO}" ]]; then
  git commit -m "$MESSAGE"
  git push origin "${GITHUB_BRANCH}"
else
  echo "Start commit"
  git config --global user.email "ci@github"
  git config --global user.name "GitHub Actions CI"
  
  git commit -m "$MESSAGE"
  
  git push --quiet "https://${GITHUB_TOKEN}:x-oauth-basic@github.com/${GITHUB_REPO}.git" "${GITHUB_BRANCH}"
fi
