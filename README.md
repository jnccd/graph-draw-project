# Documentation

## Current Progress

### Graph Loading

The parser is currently able to load .elkt files that don't contain { } blocks and text files containing more simple graph formats like:
```
n1 -> n2
n1 -> n3

n2 -> n4
n2 -> n5

n3 -> n6
n3 -> n7
```
This is enough to describe a full binary tree with a depth of 2.

Additionally also combinations of both styles are allowed, so you can add a line containing `n2 -> r` to a loadable elkt file and the parser will add the r node as well as the edge from n2 to the graph, as long as the graph is still a binary tree after r was inserted.

After a graph has been parsed the BinaryTreeCheckPhase will be invoked which does what the name suggests and any Exceptions during that check will be shown within a pop-up window.

If the check succseeds first the InorderLayoutPhase is applied to the graph and then a modified version of the RTLayoutPhase is invoked on the graph. The modified version will create a new instance of the GraphState class which contains the entire graph and additional information about the current state of the algorithm that should be shown later and add that instance to a instance of the GraphStateManager which holds and manages the list of GraphStates that will be shown as a diashow in the GUI.

### The GUI

After the List of GraphStates has been build the current GraphState is drawn to the middle panel of the main window of the application which is defined in MainFrame.java.

The current GUI also contains buttons of the very left of the bottom bar which allow you to manually cycle through the GraphStates and a play button which enables the playing state and will automatically cycle through the GraphStates, similarly to almost every video player the bar next to the play button shows the current progress through the animation.

The right side of the GUI contains two tabs.\
One of which is a Options panel which currently only allows changing the node sizes.\
The other tab contains the editor which will render the source code of the currently loaded graph. Changing the source code in that editor and hitting CTRL + S will automatically reload the graph with the new source code and also save the changes to the loaded file.\
Changing the source code before any file has been loaded with result in it being saved to a tmp file and loaded like any other graph.

## TODO

The RT Algorhithm that is currently visualized is quite messy/contains bugs and is not based on a grid that is bigger than the pixel grid like it is formulated in the original paper. It also doesn't use threads to keep the runtime within O(n).\
To fix this I should probably rewrite my RT algorithm ~~again~~ and use a grid this time. This way nodes wont partially overlap and bugs that stemmed from node sizes not being properly accounted for can't occur since we can just resize the entire grid.\
The move to a grid will also force me to rewrite the draw method in GraphState.\
Contour calculations using threads might also be implemented depending on how much time is left after all of this.

The GUI and Parser is mostly functional so I don't intend to change much there but I might add some more Options to the Options Panel.\
Maybe different node drawing/texture options similar to the ones in ELK.\
Maybe also an options that hides certain steps from the algorithm, because "level of detail" options are listed in the MUST features in the project describtion in the iLearn. This is quite easy to implement, however I don't see why that would make this program more educational so I don't know if its really nessecary.

## Additionally

#### Your documentation MUST consist of: The way you deal with additional data (i.e. layers)

I don't deal with additional data.

#### Your documentation MUST consist of: Description of the visualization you are envisioning.

I hope the TODO Section contains that to a satisfactory degree.
