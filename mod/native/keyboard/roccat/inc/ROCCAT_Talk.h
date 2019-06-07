#pragma once

#define TALKFX_ZONE_AMBIENT 0x00
#define TALKFX_ZONE_EVENT   0x01

#define TALKFX_EFFECT_OFF 0x00
#define TALKFX_EFFECT_ON  0x01
#define TALKFX_EFFECT_BLINKING  0x02
#define TALKFX_EFFECT_BREATHING 0x03
#define TALKFX_EFFECT_HEARTBEAT 0x04

#define TALKFX_SPEED_NOCHANGE 0x00
#define TALKFX_SPEED_SLOW     0x01
#define TALKFX_SPEED_NORMAL   0x02
#define TALKFX_SPEED_FAST     0x03


class CROCCAT_Talk
{
public:
			CROCCAT_Talk(void);  /* default constructor */
			~CROCCAT_Talk(void); /* default destructor */
			
	/* initiate connection to Ryos MK PRO keyboard and check if present */
	BOOL	init_ryos_talk(void);
	
	/* take control of a connected Ryos MK PRO keyboard */
	BOOL	set_ryos_kb_SDKmode(BOOL state);
	
	/* basic Ryos MK PRO LED control methods */
	void	turn_off_all_LEDS(void);
	void	turn_on_all_LEDS(void);
	
	/* turn on/off a single LED at specified position */
	void	set_LED_on(BYTE ucPos);
	void	set_LED_off(BYTE ucPos);
	
	/* send a whole array as a frame to the keyboard (manipulate mulitple LEDS)*/
	void	Set_all_LEDS(BYTE *ucLED);
	
	/* simple blinking effect on Ryos MK PRO */
	void	All_Key_Blinking(int DelayTime, int LoopTimes);
	
	/* TALK FX method -- set specified zone to effect and RGB colour */
	void	Set_LED_RGB(BYTE bZone, BYTE bEffect, BYTE bSpeed,BYTE colorR, BYTE colorG, BYTE colorB);
	
	/* TALK FX method -- restore user LED colour at end of program */
	void	RestoreLEDRGB();

protected:
	HWND	m_hwnd;
	UINT	m_uiMsgIDDiscover;

	BYTE	GetKeyNo(BYTE cMapKey);

	static HWND	m_hTalkWnd;
	static UINT m_uiMsgIDAttach, m_uiMsgIDControl;
	static UINT m_uiMsgIDAttachForFX, m_uiMsgIDControlForFX;
	UINT	m_uiMsgIDDiscoverForFX;
	static BYTE	bLedOnOff[15];
	static LRESULT CALLBACK	SDKWndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);
};
