#!/bin/bash
xgettext -k_ -o pot/messages.pot $(find . -name "*.java") --from-code utf-8
