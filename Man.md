# Man for modding

## Events

See this page for more info: [WikiPage](https://mindustrygame.github.io/wiki/modding/3-scripting/)

See this page for list of events: [WikiPage](https://github.com/Anuken/Mindustry/blob/master/core/src/mindustry/game/EventType.java)

Use example (use this in constructor of main class):

```java
// listen for the event where a unit is destroyed
Events.on(UnitDestroyEvent, event => {
  // display toast on top of screen when the unit was a player
  if(event.unit.isPlayer()){
    Vars.ui.hudfrag.showToast("Pathetic.");
  }
})
```

## Sprites

Info for create spites: [WikiPage](https://mindustrygame.github.io/wiki/modding/4-spriting/)
