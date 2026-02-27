## Question 11: Actual Terminal History (Reflog)

```powershell
# 1. Resetting to a clean state to ensure a conflict can happen
git reset --hard 16ce7b5

# 2. Creating unique work on the feature branch
git checkout ft/setup
# (Edited test.java)
git add test.java
git commit -m "feature version"

# 3. Creating competing work on the main branch
git checkout main
# (Edited test.java on the same line)
git add test.java
git commit -m "Push on main"

# 4. Merging and resolving the manual conflict
git checkout ft/setup
git merge main
# [Conflict triggered and resolved in editor]
git add test.java
git commit -m "resolve conflict"
git push origin ft/setup