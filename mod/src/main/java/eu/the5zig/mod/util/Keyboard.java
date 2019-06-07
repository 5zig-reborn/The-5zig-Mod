/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.util;

public class Keyboard {

	public static int KEY_NONE;
	public static int KEY_ESCAPE;
	public static int KEY_1;
	public static int KEY_2;
	public static int KEY_3;
	public static int KEY_4;
	public static int KEY_5;
	public static int KEY_6;
	public static int KEY_7;
	public static int KEY_8;
	public static int KEY_9;
	public static int KEY_0;
	public static int KEY_MINUS;
	public static int KEY_EQUALS;
	public static int KEY_BACK;
	public static int KEY_TAB;
	public static int KEY_Q;
	public static int KEY_W;
	public static int KEY_E;
	public static int KEY_R;
	public static int KEY_T;
	public static int KEY_Y;
	public static int KEY_U;
	public static int KEY_I;
	public static int KEY_O;
	public static int KEY_P;
	public static int KEY_LBRACKET;
	public static int KEY_RBRACKET;
	public static int KEY_RETURN;
	public static int KEY_LCONTROL;
	public static int KEY_A;
	public static int KEY_S;
	public static int KEY_D;
	public static int KEY_F;
	public static int KEY_G;
	public static int KEY_H;
	public static int KEY_J;
	public static int KEY_K;
	public static int KEY_L;
	public static int KEY_SEMICOLON;
	public static int KEY_APOSTROPHE;
	public static int KEY_GRAVE;
	public static int KEY_LSHIFT;
	public static int KEY_BACKSLASH;
	public static int KEY_Z;
	public static int KEY_X;
	public static int KEY_C;
	public static int KEY_V;
	public static int KEY_B;
	public static int KEY_N;
	public static int KEY_M;
	public static int KEY_COMMA;
	public static int KEY_PERIOD;
	public static int KEY_SLASH;
	public static int KEY_RSHIFT;
	public static int KEY_MULTIPLY;
	public static int KEY_LMENU;
	public static int KEY_SPACE;
	public static int KEY_CAPITAL;
	public static int KEY_F1;
	public static int KEY_F2;
	public static int KEY_F3;
	public static int KEY_F4;
	public static int KEY_F5;
	public static int KEY_F6;
	public static int KEY_F7;
	public static int KEY_F8;
	public static int KEY_F9;
	public static int KEY_F10;
	public static int KEY_NUMLOCK;
	public static int KEY_SCROLL;
	public static int KEY_NUMPAD7;
	public static int KEY_NUMPAD8;
	public static int KEY_NUMPAD9;
	public static int KEY_SUBTRACT;
	public static int KEY_NUMPAD4;
	public static int KEY_NUMPAD5;
	public static int KEY_NUMPAD6;
	public static int KEY_ADD;
	public static int KEY_NUMPAD1;
	public static int KEY_NUMPAD2;
	public static int KEY_NUMPAD3;
	public static int KEY_NUMPAD0;
	public static int KEY_DECIMAL;
	public static int KEY_F11;
	public static int KEY_F12;
	public static int KEY_F13;
	public static int KEY_F14;
	public static int KEY_F15;
	public static int KEY_F16;
	public static int KEY_F17;
	public static int KEY_F18;
	public static int KEY_F19;
	public static int KEY_NUMPADENTER;
	public static int KEY_RCONTROL;
	public static int KEY_SYSRQ;
	public static int KEY_RMENU;
	public static int KEY_PAUSE;
	public static int KEY_HOME;
	public static int KEY_UP;
	public static int KEY_PRIOR;
	public static int KEY_LEFT;
	public static int KEY_RIGHT;
	public static int KEY_END;
	public static int KEY_DOWN;
	public static int KEY_NEXT;
	public static int KEY_INSERT;
	public static int KEY_DELETE;
	public static int KEY_LMETA;
	public static int KEY_RMETA;

	private static KeyboardHandler handler;

	public static void initLegacy(KeyboardHandler handler) {
		Keyboard.handler = handler;

		KEY_NONE = 0;
		KEY_ESCAPE = 1;
		KEY_1 = 2;
		KEY_2 = 3;
		KEY_3 = 4;
		KEY_4 = 5;
		KEY_5 = 6;
		KEY_6 = 7;
		KEY_7 = 8;
		KEY_8 = 9;
		KEY_9 = 10;
		KEY_0 = 11;
		KEY_MINUS = 12;
		KEY_EQUALS = 13;
		KEY_BACK = 14;
		KEY_TAB = 15;
		KEY_Q = 16;
		KEY_W = 17;
		KEY_E = 18;
		KEY_R = 19;
		KEY_T = 20;
		KEY_Y = 21;
		KEY_U = 22;
		KEY_I = 23;
		KEY_O = 24;
		KEY_P = 25;
		KEY_LBRACKET = 26;
		KEY_RBRACKET = 27;
		KEY_RETURN = 28;
		KEY_LCONTROL = 29;
		KEY_A = 30;
		KEY_S = 31;
		KEY_D = 32;
		KEY_F = 33;
		KEY_G = 34;
		KEY_H = 35;
		KEY_J = 36;
		KEY_K = 37;
		KEY_L = 38;
		KEY_SEMICOLON = 39;
		KEY_APOSTROPHE = 40;
		KEY_GRAVE = 41;
		KEY_LSHIFT = 42;
		KEY_BACKSLASH = 43;
		KEY_Z = 44;
		KEY_X = 45;
		KEY_C = 46;
		KEY_V = 47;
		KEY_B = 48;
		KEY_N = 49;
		KEY_M = 50;
		KEY_COMMA = 51;
		KEY_PERIOD = 52;
		KEY_SLASH = 53;
		KEY_RSHIFT = 54;
		KEY_MULTIPLY = 55;
		KEY_LMENU = 56;
		KEY_SPACE = 57;
		KEY_CAPITAL = 58;
		KEY_F1 = 59;
		KEY_F2 = 60;
		KEY_F3 = 61;
		KEY_F4 = 62;
		KEY_F5 = 63;
		KEY_F6 = 64;
		KEY_F7 = 65;
		KEY_F8 = 66;
		KEY_F9 = 67;
		KEY_F10 = 68;
		KEY_NUMLOCK = 69;
		KEY_SCROLL = 70;
		KEY_NUMPAD7 = 71;
		KEY_NUMPAD8 = 72;
		KEY_NUMPAD9 = 73;
		KEY_SUBTRACT = 74;
		KEY_NUMPAD4 = 75;
		KEY_NUMPAD5 = 76;
		KEY_NUMPAD6 = 77;
		KEY_ADD = 78;
		KEY_NUMPAD1 = 79;
		KEY_NUMPAD2 = 80;
		KEY_NUMPAD3 = 81;
		KEY_NUMPAD0 = 82;
		KEY_DECIMAL = 83;
		KEY_F11 = 87;
		KEY_F12 = 88;
		KEY_F13 = 100;
		KEY_F14 = 101;
		KEY_F15 = 102;
		KEY_F16 = 103;
		KEY_F17 = 104;
		KEY_F18 = 105;
		KEY_F19 = 113;
		KEY_NUMPADENTER = 156;
		KEY_RCONTROL = 157;
		KEY_SYSRQ = 183;
		KEY_RMENU = 184;
		KEY_PAUSE = 197;
		KEY_HOME = 199;
		KEY_UP = 200;
		KEY_PRIOR = 201;
		KEY_LEFT = 203;
		KEY_RIGHT = 205;
		KEY_END = 207;
		KEY_DOWN = 208;
		KEY_NEXT = 209;
		KEY_INSERT = 210;
		KEY_DELETE = 211;
		KEY_LMETA = 219;
		KEY_RMETA = 220;
	}

	public static void init(KeyboardHandler handler) {
		Keyboard.handler = handler;

		KEY_NONE = -1;
		KEY_SPACE = 32;
		KEY_APOSTROPHE = 39;
		KEY_COMMA = 44;
		KEY_MINUS = 45;
		KEY_PERIOD = 46;
		KEY_SLASH = 47;
		KEY_0 = 48;
		KEY_1 = 49;
		KEY_2 = 50;
		KEY_3 = 51;
		KEY_4 = 52;
		KEY_5 = 53;
		KEY_6 = 54;
		KEY_7 = 55;
		KEY_8 = 56;
		KEY_9 = 57;
		KEY_SEMICOLON = 59;
		KEY_EQUALS = 61;
		KEY_A = 65;
		KEY_B = 66;
		KEY_C = 67;
		KEY_D = 68;
		KEY_E = 69;
		KEY_F = 70;
		KEY_G = 71;
		KEY_H = 72;
		KEY_I = 73;
		KEY_J = 74;
		KEY_K = 75;
		KEY_L = 76;
		KEY_M = 77;
		KEY_N = 78;
		KEY_O = 79;
		KEY_P = 80;
		KEY_Q = 81;
		KEY_R = 82;
		KEY_S = 83;
		KEY_T = 84;
		KEY_U = 85;
		KEY_V = 86;
		KEY_W = 87;
		KEY_X = 88;
		KEY_Y = 89;
		KEY_Z = 90;
		KEY_LBRACKET = 91;
		KEY_BACKSLASH = 92;
		KEY_RBRACKET = 93;
		KEY_GRAVE = 96;
		KEY_ESCAPE = 256;
		KEY_RETURN = 257;
		KEY_TAB = 258;
		KEY_BACK = 259;
		KEY_INSERT = 260;
		KEY_DELETE = 261;
		KEY_RIGHT = 262;
		KEY_LEFT = 263;
		KEY_DOWN = 264;
		KEY_UP = 265;
		KEY_PRIOR = 266;
		KEY_NEXT = 267;
		KEY_HOME = 268;
		KEY_END = 269;
		KEY_CAPITAL = 280;
		KEY_SCROLL = 281;
		KEY_NUMLOCK = 282;
		KEY_SYSRQ = 283;
		KEY_PAUSE = 284;
		KEY_F1 = 290;
		KEY_F2 = 291;
		KEY_F3 = 292;
		KEY_F4 = 293;
		KEY_F5 = 294;
		KEY_F6 = 295;
		KEY_F7 = 296;
		KEY_F8 = 297;
		KEY_F9 = 298;
		KEY_F10 = 299;
		KEY_F11 = 300;
		KEY_F12 = 301;
		KEY_F13 = 302;
		KEY_F14 = 303;
		KEY_F15 = 304;
		KEY_F16 = 305;
		KEY_F17 = 306;
		KEY_F18 = 307;
		KEY_F19 = 308;
		KEY_LSHIFT = 340;
		KEY_NUMPADENTER = 335;
		KEY_LCONTROL = 341;
		KEY_LMENU = 342;
		KEY_LMETA = 343;
		KEY_RSHIFT = 344;
		KEY_RCONTROL = 345;
		KEY_RMENU = 346;
		KEY_RMETA = 347;
		KEY_LMENU = 348;
		KEY_END = 348;
	}

	public static boolean isKeyDown(int key) {
		return handler.isKeyDown(key);
	}

	public static void enableRepeatEvents(boolean repeat) {
		handler.enableRepeatEvents(repeat);
	}

	public interface KeyboardHandler {
		boolean isKeyDown(int key);

		void enableRepeatEvents(boolean repeat);
	}


}
