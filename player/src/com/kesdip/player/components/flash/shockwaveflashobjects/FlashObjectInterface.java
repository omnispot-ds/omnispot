package com.kesdip.player.components.flash.shockwaveflashobjects;

import com.jniwrapper.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.jniwrapper.win32.ole.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents COM coclass FlashObjectInterface.
 */
public class FlashObjectInterface extends CoClass
{
    public static final CLSID CLASS_ID = CLSID.create("{D27CDB71-AE6D-11CF-96B8-444553540000}");

    public FlashObjectInterface()
    {
    }

    public FlashObjectInterface(FlashObjectInterface that)
    {
        super(that);
    }

    /**
     * Creates coclass and returns its default interface.
     */
    public static IFlashObjectInterface create(ClsCtx dwClsContext) throws ComException
    {
        final IFlashObjectInterfaceImpl intf = new IFlashObjectInterfaceImpl(CLASS_ID, dwClsContext);
        OleFunctions.oleRun(intf);
        return intf;
    }

    /**
     * Queries the <code>IFlashObjectInterface</code> interface from IUnknown instance.
     */
    public static IFlashObjectInterface queryInterface(IUnknown unknown) throws ComException
    {
        final IFlashObjectInterfaceImpl result = new IFlashObjectInterfaceImpl();
        unknown.queryInterface(result.getIID(), result);
        return result;
    }

    public CLSID getCLSID()
    {
        return CLASS_ID;
    }

    public Object clone()
    {
        return new FlashObjectInterface(this);
    }
}