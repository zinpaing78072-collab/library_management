import re, os, glob, xml.etree.ElementTree as ET

FX = '{http://javafx.com/fxml/1}'
RES = 'src/main/resources'
CTRL = 'src/main/java/com/example/controller'

javafiles = {}
for p in glob.glob(CTRL + '/*.java'):
    javafiles[os.path.basename(p)[:-5]] = open(p).read()


def walk(el):
    out = []
    for c in el:
        out += walk(c)
    out.append(el)
    return out


for f in sorted(glob.glob(RES + '/**/*.fxml', recursive=True)):
    txt = open(f).read()
    ns = re.search(r'xmlns="([^"]+)"', txt).group(1)
    tree = ET.fromstring(txt)
    cc = tree.get(FX + 'controller')
    short = cc.split('.')[-1] if cc else None
    ids, actions, includes = [], [], []
    for el in walk(tree):
        i = el.get(FX + 'id')
        a = el.get('onAction')
        if i:
            # An fx:id only needs a matching @FXML field when the node is
            # referenced from the controller. Nodes that carry their own
            # onAction handler are wired without a field, and fx:include ids
            # are namespace handles rather than injected fields.
            needs_field = (a is None) and el.tag != FX + 'include'
            ids.append((i, needs_field))
        if a:
            actions.append(a)
        if el.tag == FX + 'include':
            includes.append((el.get(FX + 'id'), el.get('source')))
    print('=' * 70)
    print(f, '| ns=', ns, '| ctrl=', short)
    src = javafiles.get(short, '')
    fields = set(re.findall(r'@FXML\s+(?:private|protected|public)?\s*[\w<>,\[\]\.\s]*?\s(\w+)\s*;', src))
    methods = set(re.findall(r'@FXML\s+(?:private|protected|public)?\s*(?:void|[\w<>,\[\]\.]+)\s+(\w+)\s*\(', src))
    print('  ids in fxml :', [i for i, _ in ids])
    print('  FXML fields  :', sorted(fields))
    miss = [i for i, needs_field in ids if needs_field and i not in fields]
    extra = [x for x in fields if x not in [i for i, _ in ids]]
    if miss:
        print('  !! fx:id with NO @FXML field:', miss)
    if extra:
        print('  .. @FXML field with no fx:id:', extra)
    for a in actions:
        if a.lstrip('#') not in methods:
            print('  !! onAction handler missing:', a)
    for i, s in includes:
        print('   include id=', i, 'source=', s)
