import { PLAYER_SIZE } from './constants.js';

function loadImage(src) {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve(img);
    img.onerror = (e) => reject(new Error('Failed to load image: ' + src));
    img.src = src;
  });
}

export class Animation {
  constructor(name, frames, frameRate = 10, loop = true) {
    this.name = name;
    this.frames = frames; // array of Image objects
    this.frameRate = frameRate; // frames per second
    this.loop = loop;
  }
}

export class Player {
  constructor(x = 0, y = 0, assetsPath = 'assets/player/') {
    this.x = x;
    this.y = y;
    this.assetsPath = assetsPath.endsWith('/') ? assetsPath : assetsPath + '/';
    this.animations = {}; // { name: Animation }
    this.current = null;     // current Animation
    this.frameIndex = 0;
    this.frameTimer = 0;     // seconds
    this.loaded = false;
  }

  // animDefs: { name: { frames: n, frameRate?: x, loop?: bool, filePattern?: string } }
  // default file pattern: `${assetsPath}${name}${i}.png` (i starts at 1)
  async loadAnimations(animDefs) {
    const loadPromises = [];
    for (const [name, def] of Object.entries(animDefs)) {
      const count = def.frames;
      const pattern = def.filePattern || ((i) => `${this.assetsPath}${name}${i}.png`);
      for (let i = 1; i <= count; i++) {
        const src = pattern(i);
        const p = loadImage(src).then(img => img).catch(err => { throw err; });
        loadPromises.push(p);
      }
      this.animations[name] = { placeholder: true, def };
    }

    // wait for all images across all animations
    await Promise.all(loadPromises);

    // Now actually create Animation objects with loaded images
    for (const [name, data] of Object.entries(this.animations)) {
      const def = data.def;
      const frames = [];
      const pattern = def.filePattern || ((i) => `${this.assetsPath}${name}${i}.png`);
      for (let i = 1; i <= def.frames; i++) {
        const src = pattern(i);
        const img = new Image();
        img.src = src; // browser cache will prevent re-download
        frames.push(img);
      }
      this.animations[name] = new Animation(name, frames, def.frameRate || 10, def.loop !== false);
    }

    this.loaded = true;

    if (this.animations.idle) this.play('idle');
    else this.play(Object.keys(this.animations)[0]);
  }

  play(name, reset = true) {
    const anim = this.animations[name];
    if (!anim) {
      console.warn('Animation not found:', name);
      return;
    }
    if (this.current === anim && !reset) return;
    this.current = anim;
    this.frameIndex = 0;
    this.frameTimer = 0;
  }

  update(dt) {
    if (!this.current || !this.loaded || this.current.frames.length === 0) return;
    this.frameTimer += dt;
    const frameDuration = 1 / this.current.frameRate;
    while (this.frameTimer >= frameDuration) {
      this.frameTimer -= frameDuration;
      this.frameIndex++;
      if (this.frameIndex >= this.current.frames.length) {
        if (this.current.loop) this.frameIndex = 0;
        else {
          this.frameIndex = this.current.frames.length - 1; // stay on last frame
        }
      }
    }
  }

  draw(ctx) {
    if (!this.current || !this.loaded || this.current.frames.length === 0) return;
    const img = this.current.frames[this.frameIndex];
    if (!img.complete) return;
    ctx.drawImage(img, this.x, this.y, PLAYER_SIZE.width, PLAYER_SIZE.height);
  }
}
