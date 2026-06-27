SKIPMOUNT=false
PROPFILE=false
POSTFSDATA=false
LATESTARTSERVICE=true

SETTING_KEY="display_single_pulse_eyeprotection_switch"

print_modname() {
  ui_print "*******************************"
  ui_print "        Oplus Auto DC"
  ui_print "    DisplayManager refresh guard"
  ui_print "*******************************"
}

on_install() {
  assert_device_supported
  ui_print "- Extracting module files"
  unzip -o "$ZIPFILE" -x 'META-INF/*' -d "$MODPATH" >&2
}

read_secure_setting() {
  value=$(/system/bin/settings --user 0 get secure "$SETTING_KEY" 2>/dev/null)
  case "$value" in
    0|1|2) echo "$value"; return 0 ;;
  esac

  value=$(/system/bin/cmd settings get --user 0 secure "$SETTING_KEY" 2>/dev/null)
  case "$value" in
    0|1|2) echo "$value"; return 0 ;;
  esac

  echo "unknown"
  return 1
}

assert_device_supported() {
  ui_print "- Checking ColorOS dimming setting"
  dimming_value=$(read_secure_setting)

  case "$dimming_value" in
    0)
      ui_print "  Detected classic low flicker"
      ;;
    2)
      ui_print "  Detected full-brightness low flicker"
      ;;
    1)
      ui_print "  Detected reserved dimming state"
      ;;
    *)
      ui_print "  Required secure setting was not found"
      ui_print "  Expected: $SETTING_KEY=0 or 2 for user 0"
      ui_print "  Current: $SETTING_KEY=$dimming_value"
      abort "! Device is unsupported or the setting is not exposed"
      ;;
  esac
}

set_permissions() {
  set_perm_recursive "$MODPATH" 0 0 0755 0644
  [ -f "$MODPATH/service.sh" ] && set_perm "$MODPATH/service.sh" 0 0 0755
  [ -d "$MODPATH/apk" ] && set_perm_recursive "$MODPATH/apk" 0 0 0755 0644
}
