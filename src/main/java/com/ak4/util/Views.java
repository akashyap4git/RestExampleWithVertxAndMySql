package com.ak4.util;

public class Views {

    public static class ExternalView { }

    /**
     * InternalView is a class extends external view. Properties defined with this view is displayed for internal users
     *
     */
    public static class InternalView extends ExternalView { }

    /**
     * DistributorView is a class, properties defined with this view is present for user with partner type Distributor
     *
     */
    public static class DistributorView { }

    /**
     * DVARView is a class, properties defined with this view is present for user with partner type DVAR
     *
     */
    public static class DVARView { }

    /**
     * IVARView is a class, properties defined with this view is present for user with partner type IVAR
     *
     */
    public static class IVARView { }

}
