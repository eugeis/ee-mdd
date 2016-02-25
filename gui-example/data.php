<?php
header('Content-Type: application/json');
echo '{
  "split": "vert",
  "mainViews" : ["TaskEditorView", "TaskEditor2View", "TaskEditorView", "TaskEditor2View", "TaskEditorView"],
  "panels": [
    {
      "size": 1,
      "panels" : [
        {
          "size": 1,
          "panels" : [
            {
              "view": "red",
              "size": 0.5
            },
            {
              "view": "orange",
              "size": 1.5
            }]
        },
        {
          "view": "yellow",
          "size": 1
        }
      ]
    },
    {
        "size": 1,
        "panels" : [
          {
            "size": 1,
            "panels" : [
              {
                "view": "red",
                "size": 0.5
              },
              {
                "view": "orange",
                "size": 1.5
              }]
          },
          {
            "view": "yellow",
            "size": 1
          }
        ]
    },
    {
        "size": 1,
        "panels" : [
          {
            "size": 1,
            "panels" : [
              {
                "view": "red",
                "size": 0.5
              },
              {
                "view": "orange",
                "size": 1.5
              }]
          },
          {
            "view": "yellow",
            "size": 1
          },
          {
            "view": "yellow",
            "size": 1
          }
        ]
    }]
}';
?>

<?php
/*
header('Content-Type: application/json');
echo '{
  "split": "vert",
  "panels": [
    {
      "size": 1,
      "panels" : [
        {
          "size": 1,
          "panels" : [
            {
              "view": "red",
              "tabs": ["ExplorerView", "TaskView"],
              "size": 1
            },
            {
              "view": "orange",
              "tabs": ["ExplorerView", "TaskView"],
              "size": 1
            }]
        },
        {
          "view": "yellow",
          "tabs": ["CommentView", "TaskView"],
          "size": 1
        }
      ]
    },
    {
      "view": "green",
      "tabs": ["DatabaseView"],
      "size": 1
    },
    {
      "view": "cyan",
      "tabs": ["StatistiksView"],
      "size": 1
    }]
}';*/
?>
