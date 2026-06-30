#!/system/bin/sh

MODDIR=${0%/*}
APK="$MODDIR/daemon.apk"
RUN_DIR="$MODDIR/run"
BOOT_LOG_FILE="$RUN_DIR/boot.log"
PROC_NAME="oplusautodc_daemon"
MAIN_CLASS="yangfentuozi.oplusautodc.daemon.Main"

ensure_run_dir() {
    [ -d "$RUN_DIR" ] || mkdir -p "$RUN_DIR" 2>/dev/null
    chmod 0755 "$RUN_DIR" 2>/dev/null
}

log_boot() {
    ensure_run_dir
    printf '[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S' 2>/dev/null)" "$*" >> "$BOOT_LOG_FILE"
}

mount_runtime_module_prop() {
    now="$(date '+%Y-%m-%d %H:%M:%S' 2>/dev/null)"
    description="daemon 启动；屏幕状态未知；调光：未知；刷新率：未知；等待 daemon 接管；更新：$now"
    sed -i "s|^description=.*|description=$description|" "$MODDIR/module.prop"
}

kill_existing_server() {
    /system/bin/ps -A -o PID,ARGS 2>/dev/null |
        while IFS= read -r line; do
            case "$line" in
                *"$PROC_NAME"*|*"$MAIN_CLASS"*)
                    pid=$(printf '%s\n' "$line" | awk '{print $1}')
                    case "$pid" in
                        ''|PID|*[!0-9]*|$$) ;;
                        *) kill "$pid" 2>/dev/null ;;
                    esac
                    ;;
            esac
        done
}

start_server() {
    if [ ! -f "$APK" ]; then
        log_boot "missing apk: $APK"
        return 1
    fi

    kill_existing_server
    (
        export CLASSPATH="$APK"
        exec /system/bin/app_process \
            -Djava.class.path="$APK" \
            /system/bin \
            --nice-name="$PROC_NAME" \
            "$MAIN_CLASS" \
            "--module-dir=$MODDIR"
    ) >> "$BOOT_LOG_FILE" 2>&1 &

    server_pid=$!
    log_boot "daemon started pid=$server_pid apk=$APK"
}

until [ "$(getprop sys.boot_completed 2>/dev/null)" = "1" ]; do
    sleep 1
done

ensure_run_dir
mount_runtime_module_prop
start_server
