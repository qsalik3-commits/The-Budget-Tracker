#!/bin/bash
while true; do
  if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    cp app/build/outputs/apk/release/app-release.apk BudgetTracker.apk
    break
  fi
  sleep 5
done
