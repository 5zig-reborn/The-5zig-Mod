#include <jni.h>
#include <stdio.h>
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <windows.h>
#include "roccat.h"
#include "inc/ROCCAT_Talk.h"

// defines
#define KEY_LED_GROUP_ALL      0
#define KEY_LED_GROUP_SPECIAL  1

#define MAX_KEYS		110
#define KEY_ESC			0
#define KEY_F1			1
#define KEY_F2			2
#define KEY_F3			3
#define KEY_F4			4
#define KEY_F5			5
#define KEY_F6			6
#define KEY_F7			7
#define KEY_F8			8
#define KEY_F9			9
#define KEY_F10			10
#define KEY_F11			11
#define KEY_F12			12
#define KEY_PRINT		13
#define KEY_ROLL		14
#define KEY_PAUSE		15
#define KEY_MACRO1		16
#define KEY_DEGREE		17
#define KEY_1			18
#define KEY_2			19
#define KEY_3			20
#define KEY_4			21
#define KEY_5			22
#define KEY_6			23
#define KEY_7			24
#define KEY_8			25
#define KEY_9			26
#define KEY_0			27
#define KEY_QUEST		28
#define KEY_ABOSTROPH	29
#define KEY_DELETE		30
#define KEY_INSERT		31
#define KEY_POS1		32
#define KEY_PAGE_UP		33
#define KEY_NUM_LK		34
#define KEY_NUM_DIV		35
#define KEY_NUM_MUL		36
#define KEY_NUM_SUB		37
#define KEY_MACRO2		38
#define KEY_TAB			39
#define KEY_Q			40
#define KEY_W			41
#define KEY_E			42
#define KEY_R			43
#define KEY_T			44
#define KEY_Z			45
#define KEY_U			46
#define KEY_I			47
#define KEY_O			48
#define KEY_P			49
#define KEY_Ü			50
#define KEY_ADD			51
#define KEY_ENTER		52
#define KEY_REMOVE		53
#define KEY_END			54
#define KEY_PAGE_DOWN	55
#define KEY_NUM_7		56
#define KEY_NUM_8		57
#define KEY_NUM_9		58
#define KEY_NUM_ADD		59
#define KEY_MACRO3		60
#define KEY_CAPSLOCK	61
#define KEY_A			62
#define KEY_S			63
#define KEY_D			64
#define KEY_F			65
#define KEY_G			66
#define KEY_H			67
#define KEY_J			68
#define KEY_K			69
#define KEY_L			70
#define KEY_Ö			71
#define KEY_Ä			72
#define KEY_HASHTAG		73
#define KEY_NUM_4		74
#define KEY_NUM_5		75
#define KEY_NUM_6		76
#define KEY_MACRO4		77
#define KEY_LSHIFT		78
#define KEY_ARROW_LEFT	79
#define KEY_Y			80
#define KEY_X			81
#define KEY_C			82
#define KEY_V			83
#define KEY_B			84
#define KEY_N			85
#define KEY_M			86
#define KEY_COMMA		87
#define KEY_DOT			88
#define KEY_SUBTRACT	89
#define KEY_RSHIFT		90
#define KEY_UP			91
#define KEY_NUM_1		92
#define KEY_NUM_2		93
#define KEY_NUM_3		94
#define KEY_NUM_ENTER	95
#define KEY_MACRO5		96
#define KEY_LCTRL		97
#define KEY_WIN			98
#define KEY_ALT			99
#define KEY_SPACE		100
#define KEY_ALT_GR		101
#define KEY_FN			102
#define KEY_MENU		103
#define KEY_RCTRL		104
#define KEY_LEFT		105
#define KEY_DOWN		106
#define KEY_RIGHT		107
#define KEY_NUM_0		108
#define KEY_NUM_REMOVE	109

#define KEY_PLUS		51
#define KEY_HASHTAG		73
#define KEY_TAB			39
#define KEY_CAPSLOCK	61

// fields

CROCCAT_Talk roccat;

bool initialized = false;

int m_IlluminatedKeyGroup = KEY_LED_GROUP_SPECIAL;
int m_KeyColor = 0xffffff;
int m_LastKeyColor = m_KeyColor;
bool m_ShowHealth = true;
bool m_ShowArmor = true;
float m_PlayerHealth = 0.0f;
float m_PlayerArmor = 0.0f;

bool isDamageAnimation = false;

void updateColor(int color)
{
	if (m_LastKeyColor != color) {
		roccat.Set_LED_RGB(TALKFX_ZONE_EVENT, TALKFX_EFFECT_ON, TALKFX_SPEED_NORMAL, (color >> 16) & 255, (color >> 8) & 255, color & 255);
		m_LastKeyColor = color;
	}
}

void update()
{
    if (initialized)
    {
		updateColor(m_KeyColor);

        BYTE frame_data[MAX_KEYS];

		if (m_IlluminatedKeyGroup == KEY_LED_GROUP_SPECIAL)
		{
			memset(frame_data, 0, MAX_KEYS);
			frame_data[KEY_W] = 1;
			frame_data[KEY_A] = 1;
			frame_data[KEY_S] = 1;
			frame_data[KEY_D] = 1;
			frame_data[KEY_SPACE] = 1;
		}
		else
		{
			memset(frame_data, 1, MAX_KEYS);
		}

		if (m_ShowHealth)
		{
			// Function keys as health
			UINT RemainingHealth = UINT(m_PlayerHealth * 12.0f); // 12 function keys
			UINT HealthLevel = KEY_F1;
			for (UINT i = 0; i<12; i++)
			{
				// F1 key starts from row 0, column 3 to column column 15
				frame_data[HealthLevel + i] = i < RemainingHealth;
			}
		}

		if (m_ShowArmor)
		{
			// Number keys as armor
			UINT RemainingArmor = UINT(m_PlayerArmor * 10.0f); // 10 number keys
			UINT ArmorLevel = KEY_1;
			for (UINT i = 0; i<10; i++)
			{
				// Number keys starts from row 1, column 2 to column 12
				frame_data[ArmorLevel + i] = i < RemainingArmor;
			}
		}
		roccat.Set_all_LEDS(frame_data);
    }
}

JNIEXPORT jboolean JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_init(JNIEnv *, jobject)
{
    initialized = false;
    if (!roccat.init_ryos_talk())
    {
        return false;
    }

    roccat.set_ryos_kb_SDKmode(TRUE);
    roccat.turn_off_all_LEDS();
    initialized = true;
	return true;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_unInit(JNIEnv *, jobject)
{
    roccat.RestoreLEDRGB();
    roccat.set_ryos_kb_SDKmode(FALSE);
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_setIlluminatedKeys(JNIEnv *, jobject, jint group, jint color)
{
    m_IlluminatedKeyGroup = group;
    m_KeyColor = color;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_setShowHealth(JNIEnv *, jobject, jboolean show)
{
    m_ShowHealth = show;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_setShowArmor(JNIEnv *, jobject, jboolean show)
{
    m_ShowArmor = show;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_updateHealthAndArmor(JNIEnv *, jobject, jfloat health, jfloat armor)
{
    m_PlayerHealth = health;
    m_PlayerArmor = armor;
}

DWORD WINAPI Thread_DamageFlash(LPVOID lpParameter)
{
	if (initialized)
	{
		isDamageAnimation = true;
		updateColor(0xff0000);

		roccat.turn_on_all_LEDS();
		Sleep(100);
		roccat.turn_off_all_LEDS();
		Sleep(100);
		roccat.turn_on_all_LEDS();
		Sleep(80);
		isDamageAnimation = false;
		update();
	}
	return 0;
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_onDamage(JNIEnv *, jobject)
{
	HANDLE hWorkerThread = CreateThread(NULL, 0, Thread_DamageFlash, NULL, 0, NULL);
	CloseHandle(hWorkerThread);
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_displayPotionColor(JNIEnv *, jobject, jint color)
{
	roccat.Set_LED_RGB(TALKFX_ZONE_AMBIENT, TALKFX_EFFECT_ON, TALKFX_SPEED_NORMAL, (color >> 16) & 255, (color >> 8) & 255, color & 255);
	roccat.turn_on_all_LEDS();
}

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_keyboard_RoccatController_update(JNIEnv *, jobject)
{
    update();
}