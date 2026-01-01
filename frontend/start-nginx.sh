#!/bin/sh
# referencing https://github.com/joellord/frontend-containers/blob/main/react-project/start-nginx.sh

# obtains only the environment variables that start with FBFM and formats it
# to string matching: "$FBFM_<name1>,$FBFM_<name2>,..."
target_env_vars=$(printenv | awk -F= '{print $1}' | grep '^FBFM' | sed 's/^/\$/g' | paste -sd,)

if [ -n "$target_env_vars" ]; then
    files=$(find /usr/share/nginx/html -type f -name "environment-*.js")

    # there should only be one file
    for file in $files; do
        # https://www.gnu.org/software/gettext/manual/html_node/envsubst-Invocation.html
        # updates the file by performing substitution
        cat $file | envsubst $target_env_vars | tee $file
    done
fi

nginx -g 'daemon off;'
