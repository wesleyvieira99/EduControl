#!/usr/bin/env python3
"""
EduControl Icon Generator
Pure Python — sem dependências externas.
Gera ícones para macOS (.iconset) e PWA.
"""
import struct, zlib, math, os

# ─── PNG encoder ─────────────────────────────────────────────────────────────

def _chunk(tag: bytes, data: bytes) -> bytes:
    body = tag + data
    return struct.pack('>I', len(data)) + body + struct.pack('>I', zlib.crc32(body) & 0xFFFFFFFF)

def write_png(path: str, pixels: list, w: int, h: int):
    """pixels = list of rows, each row = list of (R,G,B,A) tuples (0-255)."""
    ihdr = struct.pack('>IIBBBBB', w, h, 8, 6, 0, 0, 0)  # RGBA
    raw = bytearray()
    for row in pixels:
        raw.append(0)  # filter: None
        for r, g, b, a in row:
            raw += bytes([_c(r), _c(g), _c(b), _c(a)])
    with open(path, 'wb') as f:
        f.write(b'\x89PNG\r\n\x1a\n')
        f.write(_chunk(b'IHDR', ihdr))
        f.write(_chunk(b'IDAT', zlib.compress(bytes(raw), 9)))
        f.write(_chunk(b'IEND', b''))

def _c(v) -> int:
    return max(0, min(255, int(round(v))))

def _blend(bg, rgb, alpha):
    a = alpha / 255.0
    return (_c(bg[0]*(1-a)+rgb[0]*a), _c(bg[1]*(1-a)+rgb[1]*a), _c(bg[2]*(1-a)+rgb[2]*a), 255)

# ─── Icon renderer ────────────────────────────────────────────────────────────

def make_icon(size: int) -> list:
    s = size
    px = [[(0, 0, 0, 0)] * s for _ in range(s)]
    half = s / 2.0
    corner = s * 0.22

    # ── Rounded-rect background (deep purple → violet gradient) ──────────────
    for y in range(s):
        for x in range(s):
            dx, dy = abs(x - half), abs(y - half)
            if dx > half - corner and dy > half - corner:
                if math.hypot(dx - (half - corner), dy - (half - corner)) > corner:
                    continue
            t  = (x + y) / (2.0 * s)
            r  = _c(28 + t * 72)
            g  = _c(8  + t * 28)
            b  = _c(95 + t * 120)
            # Glass shine on top-left quadrant
            shine = max(0.0, 0.42 - y / s) * 90
            px[y][x] = (_c(r + shine*0.5), _c(g + shine*0.4), _c(b + shine*0.9), 255)

    # ── Helpers ───────────────────────────────────────────────────────────────
    def fill(x1, y1, x2, y2, rgb, alpha=255, rad=0):
        x1,y1,x2,y2 = int(round(x1)),int(round(y1)),int(round(x2)),int(round(y2))
        for fy in range(max(0,y1), min(s,y2)):
            for fx in range(max(0,x1), min(s,x2)):
                if rad:
                    cdx = min(abs(fx-x1), abs(fx-(x2-1)))
                    cdy = min(abs(fy-y1), abs(fy-(y2-1)))
                    if cdx < rad and cdy < rad and math.hypot(rad-cdx, rad-cdy) > rad:
                        continue
                if px[fy][fx][3] > 0:
                    px[fy][fx] = _blend(px[fy][fx], rgb, alpha)

    def hline(y, x1, x2, rgb, alpha=200, thick=1):
        for dy2 in range(thick):
            for fx in range(max(0, int(x1)), min(s, int(x2))):
                fy = int(y) + dy2 - thick // 2
                if 0 <= fy < s and px[fy][fx][3] > 0:
                    px[fy][fx] = _blend(px[fy][fx], rgb, alpha)

    def vline(x, y1, y2, rgb, alpha=200, thick=1):
        for dx2 in range(thick):
            for fy in range(max(0, int(y1)), min(s, int(y2))):
                fx = int(x) + dx2 - thick // 2
                if 0 <= fx < s and px[fy][fx][3] > 0:
                    px[fy][fx] = _blend(px[fy][fx], rgb, alpha)

    # ── Open book ─────────────────────────────────────────────────────────────
    bx  = s * 0.14
    by  = s * 0.22
    bw  = s * 0.72
    bh  = s * 0.56
    mid = bx + bw / 2
    rr  = max(2, int(s * 0.04))
    lw  = max(1, int(s * 0.024))
    mar = s * 0.056
    first_y = by + bh * 0.30
    gap     = bh * 0.16

    # Shadow
    fill(bx+s*.023, by+s*.023, bx+bw+s*.023, by+bh+s*.023, (10,5,40), 65, rr+2)
    # Left page (brighter white)
    fill(bx, by, mid, by+bh, (255,252,255), 250, rr)
    # Right page (slightly warmer)
    fill(mid, by, bx+bw, by+bh, (248,244,255), 248, rr)
    # Spine
    vline(mid, by+s*.04, by+bh-s*.04, (155,100,225), 220, max(2,int(s*.036)))
    # Horizontal rule lines – left page
    for i in range(4):
        hline(first_y + i*gap, bx+mar, mid-mar, (130,100,205), 155, lw)
    # Horizontal rule lines – right page
    for i in range(3):
        hline(first_y + i*gap, mid+mar, bx+bw-mar, (130,100,205), 140, lw)

    # ── "EC" initials (pixel letters) ─────────────────────────────────────────
    u   = max(1, int(s * 0.019))   # unit stroke width
    tx  = bx + bw * 0.09
    ty  = by + bh * 0.08
    eh  = u * 7                    # letter height
    ew  = u * 5                    # letter width
    TC  = (78, 52, 175)            # text color
    TA  = 230

    # Letter E
    vline(tx,             ty, ty+eh, TC, TA, u)
    hline(ty,             tx, tx+ew,       TC, TA, u)
    hline(ty+eh*0.5-u/2,  tx, tx+ew*0.76, TC, TA, u)
    hline(ty+eh-u,        tx, tx+ew,       TC, TA, u)
    # Letter C
    cx2 = tx + ew + u*2
    vline(cx2,            ty, ty+eh, TC, TA, u)
    hline(ty,             cx2, cx2+ew, TC, TA, u)
    hline(ty+eh-u,        cx2, cx2+ew, TC, TA, u)

    # ── Graduation cap (top-right of right page) ───────────────────────────────
    hx  = bx + bw * 0.745
    hy  = by + bh * 0.10
    hr  = s  * 0.082
    HAT = (88, 52, 195)
    # Board (diamond top)
    fill(hx-hr*.85, hy-hr*.85, hx+hr*.85, hy+hr*.12, HAT, 215)
    # Brim
    fill(hx-hr*1.55, hy, hx+hr*1.55, hy+hr*.46, HAT, 215)
    # Tassel
    fill(hx+hr*.85, hy-hr*.7, hx+hr*1.2, hy+hr*.55, (215,170,38), 235)

    return px

# ─── Entry point ──────────────────────────────────────────────────────────────

def main():
    base = os.path.dirname(os.path.abspath(__file__))
    root = os.path.dirname(base)

    iconset = os.path.join(root, 'EduControl.iconset')
    icons   = os.path.join(root, 'frontend', 'public', 'icons')
    pub     = os.path.join(root, 'frontend', 'public')

    os.makedirs(iconset, exist_ok=True)
    os.makedirs(icons,   exist_ok=True)

    # macOS iconset requires these exact file names
    icns_map = {
        'icon_16x16.png':      16,
        'icon_16x16@2x.png':   32,
        'icon_32x32.png':      32,
        'icon_32x32@2x.png':   64,
        'icon_128x128.png':    128,
        'icon_128x128@2x.png': 256,
        'icon_256x256.png':    256,
        'icon_256x256@2x.png': 512,
        'icon_512x512.png':    512,
        'icon_512x512@2x.png': 1024,
    }

    cache: dict = {}

    def get(sz):
        if sz not in cache:
            print(f'  Renderizando {sz}×{sz}px …')
            cache[sz] = make_icon(sz)
        return cache[sz]

    print('🎨  Gerando ícones macOS (.iconset) …')
    for name, sz in icns_map.items():
        write_png(os.path.join(iconset, name), get(sz), sz, sz)

    print('📱  Gerando ícones PWA …')
    for sz in (192, 512):
        write_png(os.path.join(icons, f'icon-{sz}x{sz}.png'), get(sz), sz, sz)

    print('🍎  Apple Touch Icon 180×180px …')
    write_png(os.path.join(icons, 'apple-touch-icon.png'), get(180), 180, 180)

    print('🔖  Favicon 32×32px …')
    write_png(os.path.join(pub, 'favicon.png'), get(32), 32, 32)

    print('✅  Ícones gerados com sucesso!\n')


if __name__ == '__main__':
    main()
