#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include <Tchar.h>

#include "razer.h"
#include "inc/RzChromaSDKDefines.h"
#include "inc/RzChromaSDKTypes.h"
#include "inc/RzErrors.h"

#ifdef _WIN64
#define CHROMASDKDLL        _T("RzChromaSDK64.dll")
#else
#define CHROMASDKDLL        _T("RzChromaSDK.dll")
#endif

#define ALL_DEVICES         0
#define KEYBOARD_DEVICES    1
#define MOUSEMAT_DEVICES    2
#define MOUSE_DEVICES       3
#define HEADSET_DEVICES     4
#define KEYPAD_DEVICES      5

#define KEY_LED_GROUP_ALL      0
#define KEY_LED_GROUP_SPECIAL  1

using namespace ChromaSDK;
using namespace ChromaSDK::Keyboard;

// constants

const GUID GUID_NULL = { 0, 0, 0, { 0, 0, 0, 0, 0, 0, 0, 0 } };
// java exception path
const char* EXCEPTION = "java/lang/Exception";

// colors!
const COLORREF BLACK = RGB(0,0,0);
const COLORREF WHITE = RGB(255,255,255);
const COLORREF RED = RGB(255,0,0);
const COLORREF GREEN = RGB(0,255,0);
const COLORREF BLUE = RGB(0,0,255);
const COLORREF YELLOW = RGB(255,255,0);
const COLORREF PURPLE = RGB(128,0,128);
const COLORREF CYAN = RGB(00,255,255);
const COLORREF ORANGE = RGB(255,165,00);
const COLORREF PINK = RGB(255,192,203);
const COLORREF GREY = RGB(125, 125, 125);

// method pointers

typedef RZRESULT (*INIT)(void);
typedef RZRESULT (*UNINIT)(void);
typedef RZRESULT (*CREATEEFFECT)(RZDEVICEID DeviceId, ChromaSDK::EFFECT_TYPE Effect, PRZPARAM pParam, RZEFFECTID *pEffectId);
typedef RZRESULT (*CREATEKEYBOARDEFFECT)(ChromaSDK::Keyboard::EFFECT_TYPE Effect, PRZPARAM pParam, RZEFFECTID *pEffectId);
typedef RZRESULT (*SETEFFECT)(RZEFFECTID EffectId);
typedef RZRESULT (*DELETEEFFECT)(RZEFFECTID EffectId);
typedef RZRESULT (*REGISTEREVENTNOTIFICATION)(HWND hWnd);
typedef RZRESULT (*UNREGISTEREVENTNOTIFICATION)(void);
typedef RZRESULT (*QUERYDEVICE)(RZDEVICEID DeviceId, ChromaSDK::DEVICE_INFO_TYPE &DeviceInfo);

INIT Init = NULL;
UNINIT UnInit = NULL;
CREATEEFFECT CreateEffect = NULL;
CREATEKEYBOARDEFFECT CreateKeyboardEffect = NULL;
SETEFFECT SetEffect = NULL;
DELETEEFFECT DeleteEffect = NULL;
QUERYDEVICE QueryDevice = NULL;

// reference to Chroma DLL
HMODULE m_ChromaSDKModule;


// fields

bool initialized = false;

int m_IlluminatedKeyGroup = KEY_LED_GROUP_SPECIAL;
COLORREF m_KeyColor = WHITE;
bool m_ShowHealth = true;
bool m_ShowArmor = true;
float m_PlayerHealth = 0.0f;
float m_PlayerArmor = 0.0f;

bool isDamageAnimation = false;

// utility methods

void throwException(JNIEnv* env, const char* className, const char* message)
{
    jclass c = env->FindClass(className);

    if (NULL == c)
    {
        c = env->FindClass(EXCEPTION);
    }

    env->ThrowNew(c, message);
}

void throwException(JNIEnv* env, const char* message)
{
    throwException(env, EXCEPTION, message);
}

long update()
{
    if (CreateKeyboardEffect && initialized && !isDamageAnimation)
    {
        Keyboard::CUSTOM_KEY_EFFECT_TYPE Effect = {};
        if (m_IlluminatedKeyGroup == KEY_LED_GROUP_SPECIAL)
        {
            Effect.Key[HIBYTE(RZKEY_W)][LOBYTE(RZKEY_W)] = 0x01000000 | m_KeyColor;
            Effect.Key[HIBYTE(RZKEY_A)][LOBYTE(RZKEY_A)] = 0x01000000 | m_KeyColor;
            Effect.Key[HIBYTE(RZKEY_S)][LOBYTE(RZKEY_S)] = 0x01000000 | m_KeyColor;
            Effect.Key[HIBYTE(RZKEY_D)][LOBYTE(RZKEY_D)] = 0x01000000 | m_KeyColor;
            Effect.Key[HIBYTE(RZKEY_SPACE)][LOBYTE(RZKEY_SPACE)] = 0x01000000 | m_KeyColor;
            Effect.Key[HIBYTE(Keyboard::RZLED_LOGO)][LOBYTE(Keyboard::RZLED_LOGO)] = 0x01000000 | m_KeyColor;
        }
        else
        {
            for(UINT row=0; row<ChromaSDK::Keyboard::MAX_ROW; row++)
            {
                for(UINT col=0; col<ChromaSDK::Keyboard::MAX_COLUMN; col++)
                {
                    Effect.Color[row][col] = m_KeyColor;
                }
            }
        }

        if (m_ShowHealth)
        {
            // Function keys as health
            UINT RemainingHealth = UINT(m_PlayerHealth * 12.0f); // 12 function keys
            COLORREF HealthColor = RGB((((12-RemainingHealth)/12.0)*255), ((RemainingHealth/12.0)*255), 0);
            UINT HealthLevel = ChromaSDK::Keyboard::RZKEY_F1;
            for(UINT i=0; i<RemainingHealth; i++)
            {
                // F1 key starts from row 0, column 3 to column column 15
                Effect.Key[HIBYTE(HealthLevel+i)][LOBYTE(HealthLevel+i)] = 0x01000000 | HealthColor;
            }
        }

        if (m_ShowArmor)
        {
            // Number keys as armor
            UINT RemainingArmor = UINT(m_PlayerArmor * 10.0f); // 10 number keys
            UINT ArmorLevel = ChromaSDK::Keyboard::RZKEY_1;
            for(UINT i=0; i<RemainingArmor; i++)
            {
                // Number keys starts from row 1, column 2 to column 12
                Effect.Key[HIBYTE(ArmorLevel+i)][LOBYTE(ArmorLevel+i)] = 0x01000000 | YELLOW;
            }
        }

        return CreateKeyboardEffect(Keyboard::CHROMA_CUSTOM_KEY, &Effect, NULL);
    }
    else
    {
        return RZRESULT_INVALID;
    }
}


JNIEXPORT jlong JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_init0(JNIEnv* env, jobject)
{
    initialized = false;
    if(m_ChromaSDKModule == NULL)
    {
        m_ChromaSDKModule = LoadLibrary(CHROMASDKDLL);
        if(m_ChromaSDKModule == NULL)
        {
            return RZRESULT_SERVICE_NOT_ACTIVE;
        }
    }

    if(Init == NULL)
    {
        RZRESULT Result = RZRESULT_INVALID;
        Init = (INIT)GetProcAddress(m_ChromaSDKModule, "Init");
        if(Init)
        {
            Result = Init();
            if(Result == RZRESULT_SUCCESS)
            {
                CreateEffect = (CREATEEFFECT)GetProcAddress(m_ChromaSDKModule, "CreateEffect");
                CreateKeyboardEffect = (CREATEKEYBOARDEFFECT)GetProcAddress(m_ChromaSDKModule, "CreateKeyboardEffect");
                SetEffect = (SETEFFECT)GetProcAddress(m_ChromaSDKModule, "SetEffect");
                DeleteEffect = (DELETEEFFECT)GetProcAddress(m_ChromaSDKModule, "DeleteEffect");
                QueryDevice = (QUERYDEVICE)GetProcAddress(m_ChromaSDKModule, "QueryDevice");

                if(CreateEffect &&
                    CreateKeyboardEffect &&
                    SetEffect &&
                    DeleteEffect &&
                    QueryDevice)
                {
                    initialized = true;

                    return RZRESULT_SUCCESS;
                }
                else
                {
                    return RZRESULT_NOT_FOUND;
                }
            }
            else
            {
                return Result;
            }
        }
        else
        {
            return RZRESULT_NOT_FOUND;
        }
    }
    else
    {
        return RZRESULT_NOT_VALID_STATE;
    }
}

JNIEXPORT jlong JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_unInit0(JNIEnv *, jobject)
{
    initialized = false;
    if(m_ChromaSDKModule != NULL)
    {
        RZRESULT Result = RZRESULT_INVALID;
        UNINIT UnInit = (UNINIT)GetProcAddress(m_ChromaSDKModule, "UnInit");
        if(UnInit)
        {
            Result = UnInit();
        }

        FreeLibrary(m_ChromaSDKModule);
        m_ChromaSDKModule = NULL;

        Init = NULL;
        UnInit = NULL;
        CreateEffect = NULL;
        CreateKeyboardEffect = NULL;
        SetEffect = NULL;
        DeleteEffect = NULL;
        QueryDevice = NULL;

        return Result;
    }
    else
    {
        Init = NULL;
        UnInit = NULL;
        CreateEffect = NULL;
        CreateKeyboardEffect = NULL;
        SetEffect = NULL;
        DeleteEffect = NULL;
        QueryDevice = NULL;

        return RZRESULT_NOT_VALID_STATE;
    }
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_setIlluminatedKeys(JNIEnv *, jobject, jint group, jint color)
{
    m_IlluminatedKeyGroup = group;
    m_KeyColor = RGB((color >> 16) & 255, (color >> 8) & 255, color & 255);
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_setShowHealth(JNIEnv *, jobject, jboolean show)
{
    m_ShowHealth = show;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_setShowArmor(JNIEnv *, jobject, jboolean show)
{
    m_ShowArmor = show;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_updateHealthAndArmor(JNIEnv *, jobject, jfloat health, jfloat armor)
{
    m_PlayerHealth = health;
    m_PlayerArmor = armor;
}

DWORD WINAPI Thread_DamageFlash(LPVOID lpParameter)
{
    if (CreateKeyboardEffect && initialized)
    {
        isDamageAnimation = true;
        ChromaSDK::Keyboard::CUSTOM_EFFECT_TYPE Effect = {};
        for(UINT row=0; row<ChromaSDK::Keyboard::MAX_ROW; row++)
        {
            for(UINT col=0; col<ChromaSDK::Keyboard::MAX_COLUMN; col++)
            {
                Effect.Color[row][col] = RED;
            }
        }

        CreateKeyboardEffect(ChromaSDK::Keyboard::CHROMA_CUSTOM, &Effect, NULL);
        Sleep(100);
        CreateKeyboardEffect(ChromaSDK::Keyboard::CHROMA_NONE, NULL, NULL);
        Sleep(100);
        CreateKeyboardEffect(ChromaSDK::Keyboard::CHROMA_CUSTOM, &Effect, NULL);
        Sleep(80);
        isDamageAnimation = false;
        update();
    }
	return 0;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_onDamage(JNIEnv *, jobject)
{
    HANDLE hWorkerThread = CreateThread(NULL, 0, Thread_DamageFlash, NULL, 0, NULL);
    CloseHandle(hWorkerThread);
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_displayPotionColor(JNIEnv *, jobject, jint color)
{
    if (CreateKeyboardEffect && initialized)
    {
        ChromaSDK::Keyboard::STATIC_EFFECT_TYPE Effect = {};
        Effect.Color = RGB((color >> 16) & 255, (color >> 8) & 255, color & 255);

        CreateKeyboardEffect(ChromaSDK::Keyboard::CHROMA_STATIC, &Effect, NULL);
    }
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RazerController_update(JNIEnv *, jobject)
{
    update();
}
