package com.kesdip.player.components.flash.shockwaveflashobjects.server;

import com.jniwrapper.*;
import com.jniwrapper.win32.*;
import com.jniwrapper.win32.automation.*;
import com.jniwrapper.win32.automation.impl.*;
import com.jniwrapper.win32.automation.server.*;
import com.jniwrapper.win32.automation.types.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.server.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents VTBL for COM interface IShockwaveFlash.
 */
public class IShockwaveFlashVTBL extends IDispatchVTBL
{
    public IShockwaveFlashVTBL(CoClassMetaInfo classMetaInfo)
    {
        super(classMetaInfo);

        addMembers(
            new VirtualMethodCallback[] {
                new VirtualMethodCallback(
                    "getReadyState",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "getTotalFrames",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "getPlaying",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setPlaying",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getQuality",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setQuality",
                    new HResult(),
                    new Parameter[] {
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "getScaleMode",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setScaleMode",
                    new HResult(),
                    new Parameter[] {
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "getAlignMode",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setAlignMode",
                    new HResult(),
                    new Parameter[] {
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "getBackgroundColor",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setBackgroundColor",
                    new HResult(),
                    new Parameter[] {
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "getLoop",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setLoop",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getMovie",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setMovie",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getFrameNum",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setFrameNum",
                    new HResult(),
                    new Parameter[] {
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "setZoomRect",
                    new HResult(),
                    new Parameter[] {
                        new Int32(),
                        new Int32(),
                        new Int32(),
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "zoom",
                    new HResult(),
                    new Parameter[] {
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "pan",
                    new HResult(),
                    new Parameter[] {
                        new Int32(),
                        new Int32(),
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "play",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "stop",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "back",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "forward",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "rewind",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "stopPlay",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "gotoFrame",
                    new HResult(),
                    new Parameter[] {
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "currentFrame",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "isPlaying",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "percentLoaded",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "frameLoaded",
                    new HResult(),
                    new Parameter[] {
                        new Int32(),
                        new Pointer(new VariantBool())
                    },
                    1
                ),
                new VirtualMethodCallback(
                    "flashVersion",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "getWMode",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setWMode",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getSAlign",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setSAlign",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getMenu",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setMenu",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getBase",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setBase",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getScale",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setScale",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getDeviceFont",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setDeviceFont",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getEmbedMovie",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setEmbedMovie",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getBGColor",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setBGColor",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getQuality2",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setQuality2",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "loadMovie",
                    new HResult(),
                    new Parameter[] {
                        new Int(),
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "TGotoFrame",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "TGotoLabel",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "TCurrentFrame",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Pointer(new Int32())
                    },
                    1
                ),
                new VirtualMethodCallback(
                    "TCurrentLabel",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Pointer(new BStr())
                    },
                    1
                ),
                new VirtualMethodCallback(
                    "TPlay",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "TStopPlay",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "setVariable",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getVariable",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Pointer(new BStr())
                    },
                    1
                ),
                new VirtualMethodCallback(
                    "TSetProperty",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int(),
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "TGetProperty",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int(),
                        new Pointer(new BStr())
                    },
                    2
                ),
                new VirtualMethodCallback(
                    "TCallFrame",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int()
                    }
                ),
                new VirtualMethodCallback(
                    "TCallLabel",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "TSetPropertyNum",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int(),
                        new DoubleFloat()
                    }
                ),
                new VirtualMethodCallback(
                    "TGetPropertyNum",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int(),
                        new Pointer(new DoubleFloat())
                    },
                    2
                ),
                new VirtualMethodCallback(
                    "TGetPropertyAsNumber",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Int(),
                        new Pointer(new DoubleFloat())
                    },
                    2
                ),
                new VirtualMethodCallback(
                    "getSWRemote",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setSWRemote",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getFlashVars",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setFlashVars",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getAllowScriptAccess",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setAllowScriptAccess",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getMovieData",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setMovieData",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getInlineData",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new IUnknownImpl())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setInlineData",
                    new HResult(),
                    new Parameter[] {
                        new IUnknownImpl()
                    }
                ),
                new VirtualMethodCallback(
                    "getSeamlessTabbing",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setSeamlessTabbing",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "enforceLocalSecurity",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "getProfile",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new VariantBool())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setProfile",
                    new HResult(),
                    new Parameter[] {
                        new VariantBool()
                    }
                ),
                new VirtualMethodCallback(
                    "getProfileAddress",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setProfileAddress",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getProfilePort",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new Int32())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setProfilePort",
                    new HResult(),
                    new Parameter[] {
                        new Int32()
                    }
                ),
                new VirtualMethodCallback(
                    "callFunction",
                    new HResult(),
                    new Parameter[] {
                        new BStr(),
                        new Pointer(new BStr())
                    },
                    1
                ),
                new VirtualMethodCallback(
                    "setReturnValue",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "disableLocalSecurity",
                    new HResult(),
                    new Parameter[] {
                    }
                ),
                new VirtualMethodCallback(
                    "getAllowNetworking",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setAllowNetworking",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                ),
                new VirtualMethodCallback(
                    "getAllowFullScreen",
                    new HResult(),
                    new Parameter[] {
                        new Pointer(new BStr())
                    },
                    0
                ),
                new VirtualMethodCallback(
                    "setAllowFullScreen",
                    new HResult(),
                    new Parameter[] {
                        new BStr()
                    }
                )
            }
        );
    }
}