# flocon-no-op turns Flocon off: R8 treats the switch as always off,
# and removes the code behind it (connection, plugins, interceptors) from the app.
-assumevalues class io.github.openflocon.flocon.FloconRuntime {
    public static boolean isEnabled() return false;
}

# the marker that turns Flocon off at runtime, for builds that are not minified
-keep class io.github.openflocon.flocon.noop.FloconNoOpMarker
